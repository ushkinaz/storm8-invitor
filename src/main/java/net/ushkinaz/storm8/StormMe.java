package net.ushkinaz.storm8;

import com.db4o.ObjectContainer;
import com.db4o.config.ConfigScope;
import com.db4o.defragment.Defragment;
import com.db4o.defragment.DefragmentConfig;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import net.ushkinaz.storm8.digger.CodesDigger;
import net.ushkinaz.storm8.domain.ClanInvite;
import net.ushkinaz.storm8.domain.Game;
import net.ushkinaz.storm8.http.ServerWorkflowException;
import net.ushkinaz.storm8.invite.InviteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class StormMe {
    @SuppressWarnings({"UnusedDeclaration"})
    private static final Logger LOGGER = LoggerFactory.getLogger(StormMe.class);

    private StormConfigurator configurator;
    private InviteService service;
    private CodesDigger digger;
    private static final String STORM_DB = "storm8.db";

    @Inject
    private void StormMe(StormConfigurator configurator, InviteService service, CodesDigger digger) {
        this.configurator = configurator;
        this.service = service;
        this.digger = digger;
    }

    public static void main(String[] args) throws Exception {
        Set<String> arguments = new HashSet<String>(Arrays.asList(args));

        if (arguments.contains("defragment")) {
            DefragmentConfig defragmentConfig = new DefragmentConfig(STORM_DB);
            defragmentConfig.db4oConfig().generateUUIDs(ConfigScope.GLOBALLY);
            defragmentConfig.db4oConfig().generateVersionNumbers(ConfigScope.GLOBALLY);
            Defragment.defrag(defragmentConfig);
        }

        final Storm8Module storm8Module = new Storm8Module(STORM_DB);
        Injector injector = Guice.createInjector(storm8Module);

        StormMe stormMe = injector.getInstance(StormMe.class);

        if (arguments.contains("clean")) {
            ObjectContainer db = injector.getInstance(ObjectContainer.class);
            for (Object o : db.queryByExample(Game.class)) {
                db.delete(o);
            }
            for (Object o : db.queryByExample(ClanInvite.class)) {
                db.delete(o);
            }
            db.commit();
        }

        if (arguments.contains("dig")) {
            stormMe.dig();
        }

        if (arguments.contains("invite")) {
            stormMe.invite();
        }

//        storm8Module.shutdown();

    }

    private void dig() throws ServerWorkflowException {
        Game game = configurator.getGame("ninja");

        digger.digCodes(game);

/*
        for (Game game : configurator.getGames().values()) {
            digger.digCodes(game);
        }
*/
    }

    private void invite() throws ServerWorkflowException {
        Game game = configurator.getGame("ninja");
        service.invite(game);

/*
        for (Game game : configurator.getGames().values()) {
            service.invite(game);
        }
*/
    }
}
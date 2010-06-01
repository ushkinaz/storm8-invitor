package net.ushkinaz.storm8;

import com.db4o.ObjectContainer;
import com.db4o.config.ConfigScope;
import com.db4o.defragment.Defragment;
import com.db4o.defragment.DefragmentConfig;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import net.ushkinaz.storm8.digger.CodesDigger;
import net.ushkinaz.storm8.digger.EquipmentAnalyzerService;
import net.ushkinaz.storm8.digger.annotations.GetCodesLive;
import net.ushkinaz.storm8.digger.annotations.OfficialForum;
import net.ushkinaz.storm8.domain.ClanInvite;
import net.ushkinaz.storm8.domain.Configuration;
import net.ushkinaz.storm8.domain.Game;
import net.ushkinaz.storm8.domain.Player;
import net.ushkinaz.storm8.http.ServerWorkflowException;
import net.ushkinaz.storm8.invite.InviteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class StormMe {
// ------------------------------ FIELDS ------------------------------

    @SuppressWarnings({"UnusedDeclaration"})
    private static final Logger LOGGER = LoggerFactory.getLogger(StormMe.class);
    private static final String STORM_DB = "storm8.db";

    private InviteService service;
    private CodesDigger forumDigger;
    private CodesDigger codesDigger;
    private Configuration configuration;
    private EquipmentAnalyzerService equipmentAnalyzerService;
    private ClanBrowser clanBrowser;

// --------------------- GETTER / SETTER METHODS ---------------------


    @Inject
    public void setClanBrowser(ClanBrowser clanBrowser) {
        this.clanBrowser = clanBrowser;
    }

    @Inject
    public void setCodesDigger(@GetCodesLive CodesDigger codesDigger) {
        this.codesDigger = codesDigger;
    }

    @Inject
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    @Inject
    public void setEquipmentAnalyzerService(EquipmentAnalyzerService equipmentAnalyzerService) {
        this.equipmentAnalyzerService = equipmentAnalyzerService;
    }

    @Inject
    public void setForumDigger(@OfficialForum CodesDigger forumDigger) {
        this.forumDigger = forumDigger;
    }

    @Inject
    public void setService(InviteService service) {
        this.service = service;
    }

// -------------------------- OTHER METHODS --------------------------

    public StormMe() {
    }


// --------------------------- main() method ---------------------------

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

        if (arguments.contains("inventory")) {
            stormMe.inventory();
        }

        if (arguments.contains("scan-targets")) {
            stormMe.scanTargets();
        }

        if (arguments.contains("dig")) {
            stormMe.dig();
        }

        if (arguments.contains("invite")) {
            stormMe.invite();
        }
    }

    private void scanTargets() {
        Player player = configuration.getPlayer("ush-ninja");
        clanBrowser.setPlayer(player);
        clanBrowser.getGoodTarget();
    }

    private void inventory() {
        Player player = configuration.getPlayer("ush-ninja");
        equipmentAnalyzerService.dig(player);
    }

    private void dig() throws ServerWorkflowException {
        Game game = configuration.getGame("ninja");

        forumDigger.digCodes(game);
        codesDigger.digCodes(game);

/*
        for (Game game : configurator.getGames().values()) {
            digger.digCodes(game);
        }
*/
    }

    private void invite() throws ServerWorkflowException {
        Player player = configuration.getPlayer("ush-ninja");
        service.invite(player);

/*
        for (Game game : configurator.getGames().values()) {
            service.invite(game);
        }
*/
    }
}
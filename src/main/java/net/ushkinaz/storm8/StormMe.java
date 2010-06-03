package net.ushkinaz.storm8;

import com.db4o.ObjectContainer;
import com.db4o.config.ConfigScope;
import com.db4o.defragment.Defragment;
import com.db4o.defragment.DefragmentConfig;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import net.ushkinaz.storm8.digger.CodesDigger;
import net.ushkinaz.storm8.digger.annotations.GetCodesLive;
import net.ushkinaz.storm8.digger.annotations.HitList;
import net.ushkinaz.storm8.digger.annotations.OfficialForum;
import net.ushkinaz.storm8.domain.ClanInvite;
import net.ushkinaz.storm8.domain.Configuration;
import net.ushkinaz.storm8.domain.Game;
import net.ushkinaz.storm8.domain.Player;
import net.ushkinaz.storm8.explorer.*;
import net.ushkinaz.storm8.guice.PlayerProvider;
import net.ushkinaz.storm8.guice.Storm8Module;
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

    private CodesDigger forumDigger;
    private CodesDigger codesDigger;
    private Configuration configuration;
    private Injector injector;

// --------------------- GETTER / SETTER METHODS ---------------------


    @Inject
    public void setInjector(Injector injector) {
        this.injector = injector;
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
    public void setForumDigger(@OfficialForum CodesDigger forumDigger) {
        this.forumDigger = forumDigger;
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

        if (arguments.contains("dig-comments")) {
            stormMe.digComments();
        }

        if (arguments.contains("invite")) {
            stormMe.invite();
        }
    }

    private void digComments() {
        Player player = configuration.getPlayer("ush-ninja");
        injector.getInstance(PlayerProvider.class).setPlayer(player);
        ProfileCodesDigger profileCodesDigger = injector.getInstance(ProfileCodesDigger.class);
//        profileCodesDigger.setPlayer(player);

//        VictimsScanner victimsScanner = injector.getInstance(ClanScanner.class);
//        victimsScanner.visitVictims(profileCodesDigger);

        VictimsScanner hitListScanner = injector.getInstance(Key.get(VictimsScanner.class, HitList.class));
        hitListScanner.visitVictims(profileCodesDigger);
    }

    private void scanTargets() {
    }

    private void inventory() {
        Player player = configuration.getPlayer("ush-ninja");

        EquipmentAnalyzerService equipmentAnalyzerService = injector.getInstance(EquipmentAnalyzerService.class);
        equipmentAnalyzerService.dig(player);
    }

    private void dig() throws ServerWorkflowException {
        Game game = configuration.getGame("ninja");

        forumDigger.digCodes(game);
        codesDigger.digCodes(game);
    }

    private void invite() throws ServerWorkflowException {
        Player player = configuration.getPlayer("ush-ninja");
        injector.getInstance(PlayerProvider.class).setPlayer(player);
        InviteService service = injector.getInstance(InviteService.class);
        service.invite(player);

/*
        for (Game game : configurator.getGames().values()) {
            service.invite(game);
        }
*/
    }
}
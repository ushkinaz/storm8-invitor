package net.ushkinaz.storm8.guice;

/*
* Created by IntelliJ IDEA.
* User: Dmitry Sidorenko
* Date: 23.05.2010
* Time: 23:30:39
*/

import com.db4o.ObjectContainer;
import com.google.inject.AbstractModule;
import javolution.xml.XMLBinding;
import net.ushkinaz.storm8.configuration.StormConfigurator;
import net.ushkinaz.storm8.dao.DB4OProvider;
import net.ushkinaz.storm8.digger.CodesDigger;
import net.ushkinaz.storm8.digger.LiveCodesDigger;
import net.ushkinaz.storm8.digger.annotations.Clan;
import net.ushkinaz.storm8.digger.annotations.GetCodesLive;
import net.ushkinaz.storm8.digger.annotations.HitList;
import net.ushkinaz.storm8.digger.annotations.OfficialForum;
import net.ushkinaz.storm8.digger.forum.ForumCodesDigger;
import net.ushkinaz.storm8.domain.Configuration;
import net.ushkinaz.storm8.domain.Player;
import net.ushkinaz.storm8.domain.xml.XMLBinderFactory;
import net.ushkinaz.storm8.explorer.ClanScanner;
import net.ushkinaz.storm8.explorer.HitListScanner;
import net.ushkinaz.storm8.explorer.VictimsScanner;
import net.ushkinaz.storm8.invite.InviteService;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public class Storm8Module extends AbstractModule {
// ------------------------------ FIELDS ------------------------------

    @SuppressWarnings({"UnusedDeclaration"})
    private static final Logger LOGGER = getLogger(InviteService.class);
    protected String dbFile;

    private DB4OProvider db4oOProvider;
    private XMLBinderFactory xmlBinderFactory;
    private PlayerProvider playerProvider;

// --------------------------- CONSTRUCTORS ---------------------------

    public Storm8Module(String dbFile) {
        this.dbFile = dbFile;
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public DB4OProvider getDb4oOProvider() {
        return db4oOProvider;
    }

// -------------------------- OTHER METHODS --------------------------

    protected void configure() {
        bind(CodesDigger.class).to(ForumCodesDigger.class);

        xmlBinderFactory = new XMLBinderFactory();
        bind(XMLBinding.class).toProvider(xmlBinderFactory);

        db4oOProvider = createDB4OProvider();
        bind(ObjectContainer.class).toProvider(db4oOProvider);

        bind(DB4OProvider.class).toInstance(db4oOProvider);

        playerProvider = new PlayerProvider();
        bind(Player.class).toProvider(playerProvider);

        bind(PlayerProvider.class).toInstance(playerProvider);

        bind(Configuration.class).toProvider(StormConfigurator.class);

        bind(CodesDigger.class).annotatedWith(OfficialForum.class).to(ForumCodesDigger.class);
        bind(CodesDigger.class).annotatedWith(GetCodesLive.class).to(LiveCodesDigger.class);

        bind(VictimsScanner.class).annotatedWith(Clan.class).to(ClanScanner.class);

        bind(VictimsScanner.class).annotatedWith(HitList.class).to(HitListScanner.class);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                shutdown();
            }
        });
    }

    protected DB4OProvider createDB4OProvider() {
        return new DB4OProvider(dbFile);
    }

    public void shutdown() {
        db4oOProvider.shutdown();
    }
}

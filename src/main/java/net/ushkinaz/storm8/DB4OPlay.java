package net.ushkinaz.storm8;

import com.db4o.ObjectContainer;
import com.google.inject.Guice;
import com.google.inject.Injector;
import net.ushkinaz.storm8.domain.ClanInvite;
import net.ushkinaz.storm8.domain.ClanInviteSource;
import net.ushkinaz.storm8.domain.Game;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;

/**
 * Convertor from HSQL to db4obj
 *
 * @author Dmitry Sidorenko
 */
public class DB4OPlay {
    @SuppressWarnings({"UnusedDeclaration"})
    private static final Logger LOGGER = LoggerFactory.getLogger(DB4OPlay.class);

    public static void main(String[] args) throws IOException, SQLException {
        Injector injector = Guice.createInjector(new Storm8Module("storm8.db"));
        ObjectContainer db = injector.getInstance(ObjectContainer.class);


        db.ext().backup("storm8.bak");

        StormConfigurator configurator = injector.getInstance(StormConfigurator.class);

        Game ninjaGame = configurator.getGame("ninja");

        Collection<ClanInvite> set = db.queryByExample(ClanInvite.class);
        for (ClanInvite clanInvite : set) {
            if (clanInvite.getInviteSource() == null) {
                LOGGER.info("Clan :" + clanInvite);
                clanInvite.setInviteSource(ClanInviteSource.LIVE_CODES);
            }

            db.store(clanInvite);
        }
        db.close();
    }
}

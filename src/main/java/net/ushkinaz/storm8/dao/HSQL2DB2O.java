package net.ushkinaz.storm8.dao;

import com.db4o.ObjectContainer;
import com.google.inject.Guice;
import com.google.inject.Injector;
import net.ushkinaz.storm8.Storm8Module;
import net.ushkinaz.storm8.StormConfigurator;
import net.ushkinaz.storm8.domain.ClanInvite;
import net.ushkinaz.storm8.domain.ClanInviteStatus;
import net.ushkinaz.storm8.domain.Game;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Convertor from HSQL to db4obj
 *
 * @author Dmitry Sidorenko
 */
public class HSQL2DB2O {
    private static final org.apache.commons.logging.Log LOGGER = org.apache.commons.logging.LogFactory.getLog(HSQL2DB2O.class);

    public static void main(String[] args) throws IOException, SQLException {
        Injector injector = Guice.createInjector(new Storm8Module("storm8.db"));
        ObjectContainer db = injector.getInstance(ObjectContainer.class);


        db.ext().backup("storm8.bak");

        StormConfigurator configurator = injector.getInstance(StormConfigurator.class);

        Game ninjaGame = configurator.getGame("Ninja");

        for (Game game : configurator.getGames().values()) {
            db.store(game);
        }
        db.commit();


        final DBConnector dbConnector = new DBConnector("Storm.db");
        Connection connection = dbConnector.get();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                dbConnector.shutdown();
            }
        });

        String queryString;
        queryString = "SELECT * FROM Clans";

        ResultSet set = connection.createStatement().executeQuery(queryString);

        try {
            while (set.next()) {
                try {
                    ClanInvite clanInvite = new ClanInvite();
                    clanInvite.setCode(set.getString("CODE"));
                    clanInvite.setName(set.getString("NAME"));
                    clanInvite.setDateRequested(set.getDate("DATE_REQUESTED"));
                    clanInvite.setDateUpdated(set.getDate("DATE_UPDATED"));
                    clanInvite.setStatus(ClanInviteStatus.getByStatus(set.getInt("STATUS")));
                    clanInvite.setGame(ninjaGame);

                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug(clanInvite.getCode() + " : " + clanInvite.getDateRequested());
                    }
//                    db.store(clanInvite);
                } catch (SQLException e) {
                    LOGGER.error("Error", e);
                }
            }
        } catch (SQLException e) {
            LOGGER.error(e);
        } finally {
            db.commit();
            set.close();
        }
    }
}

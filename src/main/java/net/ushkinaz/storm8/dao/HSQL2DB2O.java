package net.ushkinaz.storm8.dao;

import com.db4o.ObjectContainer;
import com.google.inject.Guice;
import com.google.inject.Injector;
import net.ushkinaz.storm8.Storm8Module;
import net.ushkinaz.storm8.StormConfigurator;
import net.ushkinaz.storm8.domain.ClanInvite;
import net.ushkinaz.storm8.domain.ClanInviteStatus;
import net.ushkinaz.storm8.domain.Game;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;

/**
 * Convertor from HSQL to db4obj
 *
 * @author Dmitry Sidorenko
 */
public class HSQL2DB2O {
    private static final org.apache.commons.logging.Log LOGGER = org.apache.commons.logging.LogFactory.getLog(HSQL2DB2O.class);
    private static final String STORM8_DB = "storm8.db";

    public static void main(String[] args) throws IOException, SQLException {
        //Clear DB
        new File(STORM8_DB).delete();

        Injector injector = Guice.createInjector(new Storm8Module(STORM8_DB));
        final ObjectContainer db = injector.getInstance(ObjectContainer.class);


//        db.ext().backup("storm8.bak");

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
                db.close();
            }
        });

        String queryString;
        queryString = "SELECT * FROM Clans";

        ResultSet set = connection.createStatement().executeQuery(queryString);

        try {
            int count = 0;
            while (set.next()) {
                try {
                    ClanInvite clanInvite = new ClanInvite();
                    clanInvite.setCode(set.getString("CODE").toUpperCase());
                    String name = set.getString("NAME");
                    //Bug workaround
                    if (name != null && !name.equals("I")) {
                        clanInvite.setName(name);
                    }
                    clanInvite.setDateRequested(set.getDate("DATE_REQUESTED"));
                    clanInvite.setDateUpdated(set.getDate("DATE_UPDATED"));
                    clanInvite.setStatus(ClanInviteStatus.getByStatus(set.getInt("STATUS")));
                    clanInvite.setGame(ninjaGame);

                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug(MessageFormat.format("{2}: {0} know as  {1} : ", clanInvite.getCode(), (clanInvite.getName() != null) ? clanInvite.getName() : "NoBody", clanInvite.getDateRequested()));
                    }
                    db.store(clanInvite);
                    count++;
                } catch (SQLException e) {
                    LOGGER.error("Error", e);
                }
            }
            LOGGER.debug(MessageFormat.format("Converted {0} invites", count));
        } catch (SQLException e) {
            LOGGER.error(e);
        } finally {
            db.commit();
            set.close();
        }
    }
}

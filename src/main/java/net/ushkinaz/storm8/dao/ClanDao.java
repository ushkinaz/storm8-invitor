package net.ushkinaz.storm8.dao;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.ushkinaz.storm8.domain.ClanInviteStatus;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.slf4j.LoggerFactory.getLogger;

@Singleton
public class ClanDao {
    private static final Logger LOGGER = getLogger(ClanDao.class);

    private Connection connection;

    @Inject
    public ClanDao(Connection connection) {
        this.connection = connection;
    }


    public void insertNewClan(String clanCode, String gameCode) {
        try {
            connection.createStatement().executeUpdate(String.format("INSERT INTO CLANS (code, game, date_requested) VALUES ('%s, %s', CURRENT_TIMESTAMP )", clanCode, gameCode));
        } catch (SQLException e) {
            LOGGER.debug("Already there?", e);
        }
    }

    public void updateClanDB(String clanCode, String clanName, ClanInviteStatus status, String gameCode) {
        String updateString;
        updateString = String.format("SET Status=%d, date_updated = CURRENT_TIMESTAMP", status.getStatus());
        if (clanName != null) {
            updateString += String.format(", name='%s'", clanName);
        }
        try {
            String updateQuery = String.format("UPDATE Clans %s WHERE code = '%s' AND GAME='%s'", updateString, clanCode, gameCode);
            connection.createStatement().executeUpdate(updateQuery);
        } catch (SQLException e) {
            LOGGER.error("Error", e);
        }
    }

    public boolean isInvited(String clanCode, String gameCode) {
        String queryString = String.format("SELECT code FROM Clans WHERE Status is NOT NULL AND code='%s' AND GAME = '%s'", clanCode, gameCode);
        try {
            ResultSet set = connection.createStatement().executeQuery(queryString);
            return set.next();
        } catch (SQLException e) {
            LOGGER.error("Error", e);
        }
        return false;
    }

    public ResultSet getByStatus(ClanInviteStatus status, String gameCode) {
        String queryString;
        if (status == null) {
            queryString = "SELECT code FROM Clans WHERE Status IS NULL";
        } else {
            queryString = "SELECT code FROM Clans WHERE Status = " + status.getStatus();
        }

        queryString += String.format(" AND GAME = '%s'", gameCode);

        try {
            return connection.createStatement().executeQuery(queryString);
        } catch (SQLException e) {
            LOGGER.error("Error", e);
            return null;
        }
    }
}
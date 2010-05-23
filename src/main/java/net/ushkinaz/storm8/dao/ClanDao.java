package net.ushkinaz.storm8.dao;

import com.google.inject.Singleton;
import net.ushkinaz.storm8.invite.ClanInviteStatus;
import org.slf4j.Logger;

import java.io.IOException;
import java.sql.*;

import static org.slf4j.LoggerFactory.getLogger;

@Singleton
public class ClanDao {
    private static final Logger LOGGER = getLogger(ClanDao.class);

    private Connection conn;
    private static final String DB_LOCATION = "c:/temp/inviordb";

    public ClanDao() throws IOException {
        initDB();
    }

    public void initDB() throws IOException {
        try {
            try {
                Class.forName("org.hsqldb.jdbcDriver");
            } catch (ClassNotFoundException e) {
                LOGGER.error("ERROR: failed to load HSQLDB JDBC driver.", e);
                throw new IOException(e);
            }
            conn = DriverManager.getConnection("jdbc:hsqldb:file:" + DB_LOCATION + ";shutdown=true", "SA", "");
        } catch (SQLException e) {
            LOGGER.error("Error", e);
            throw new IOException(e);
        }
    }

    public void shutdown() {
        try {
            Statement st = conn.createStatement();
            st.execute("SHUTDOWN");
            conn.close();    // if there are no other open connection
        } catch (SQLException e) {
            LOGGER.error("Error", e);
        }
    }

    public void insertNewClan(String clanCode) {
        try {
            conn.createStatement().executeUpdate("INSERT INTO CLANS (code, date_requested) VALUES ('" + clanCode + "', CURRENT_TIMESTAMP )");
        } catch (SQLException e) {
            LOGGER.debug("Already there?", e);
        }
    }

    public void updateClanDB(String clanCode, String clanName, ClanInviteStatus status) {
        String updateString;
        updateString = "SET Status=" + status.getStatus() + ", date_updated = CURRENT_TIMESTAMP";
        if (clanName != null) {
            updateString += ", name='" + clanName + "'";
        }
        try {
            String updateQuery = "UPDATE Clans " + updateString + " WHERE code = '" + clanCode + "'";
            conn.createStatement().executeUpdate(updateQuery);
        } catch (SQLException e) {
            LOGGER.error("Error", e);
        }
    }

    public boolean isInvited(String clanCode) {
        String queryString = "SELECT code FROM Clans WHERE Status is NOT NULL AND code='" + clanCode + "'";
        try {
            ResultSet set = conn.createStatement().executeQuery(queryString);
            return set.next();
        } catch (SQLException e) {
            LOGGER.error("Error", e);
        }
        return false;
    }
}
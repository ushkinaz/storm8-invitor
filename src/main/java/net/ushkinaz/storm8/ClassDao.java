package net.ushkinaz.storm8;

import net.ushkinaz.storm8.invite.ClanInviteStatus;
import org.slf4j.Logger;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.slf4j.LoggerFactory.getLogger;

public class ClassDao {
    private static final Logger LOGGER = getLogger(ClassDao.class);

    private Connection conn;
    private static final String DB_LOCATION = "c:/temp/inviordb";

    public ClassDao() {
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
}
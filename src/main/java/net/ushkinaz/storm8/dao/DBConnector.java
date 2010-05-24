package net.ushkinaz.storm8.dao;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import org.slf4j.Logger;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.slf4j.LoggerFactory.getLogger;

@Singleton
public class DBConnector implements Provider<Connection> {
    private static final Logger LOGGER = getLogger(DBConnector.class);

    static final String JDBC_DRIVER = "org.hsqldb.jdbcDriver";
    static final String DB_LOCATION = "Storm.db";
    private Connection conn;

    @Inject
    public DBConnector() throws IOException {
        //TODO: avoid IO in guice module init
        initDB();
    }

    protected void initDB() throws IOException {
        try {
            try {
                Class.forName(JDBC_DRIVER);
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

    public Connection get() {
        return conn;
    }
}
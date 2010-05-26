package net.ushkinaz.storm8.dao;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.config.EmbeddedConfiguration;
import com.db4o.constraints.UniqueFieldValueConstraint;
import com.google.inject.Provider;
import net.ushkinaz.storm8.domain.ClanInvite;
import net.ushkinaz.storm8.domain.Game;

/**
 * @author Dmitry Sidorenko
 * @date May 25, 2010
 */
public class DB4OProvider implements Provider<ObjectContainer> {
    private static final org.apache.commons.logging.Log LOGGER = org.apache.commons.logging.LogFactory.getLog(DB4OProvider.class);

    private EmbeddedConfiguration configuration;
    private ObjectContainer db;

    public DB4OProvider(String dbFile) {
        LOGGER.info("Initializing DB");
        configuration = Db4oEmbedded.newConfiguration();

        configureDatabase();

        db = Db4oEmbedded.openFile(configuration, dbFile);

        db.ext().backup("storm8.bak");
    }


    public ObjectContainer get() {
        return db;
    }

    public synchronized void shutdown() {
        if (db != null && !db.ext().isClosed()) {
            LOGGER.info("DB shutdown");
            db.close();
            LOGGER.info("DB shutdown done");
            db = null;
        }
    }

    private void configureDatabase() {
        configuration.common().objectClass(ClanInvite.class).objectField("code").indexed(true);

        configuration.common().objectClass(Game.class).objectField("domain").indexed(true);
        configuration.common().add(new UniqueFieldValueConstraint(Game.class, "domain"));
    }

}

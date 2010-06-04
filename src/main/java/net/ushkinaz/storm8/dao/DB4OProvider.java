package net.ushkinaz.storm8.dao;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.config.EmbeddedConfiguration;
import com.db4o.constraints.UniqueFieldValueConstraint;
import com.google.inject.Provider;
import net.ushkinaz.storm8.domain.Game;
import net.ushkinaz.storm8.domain.Topic;
import net.ushkinaz.storm8.domain.xml.XMLDBFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dmitry Sidorenko
 */
public class DB4OProvider implements Provider<ObjectContainer> {
// ------------------------------ FIELDS ------------------------------

    @SuppressWarnings({"UnusedDeclaration"})
    private static final Logger LOGGER = LoggerFactory.getLogger(DB4OProvider.class);

    private EmbeddedConfiguration configuration;
    private ObjectContainer db;
    private List<DBConsumer> consumers;

// --------------------------- CONSTRUCTORS ---------------------------

    public DB4OProvider(String dbFile) {
        consumers = new ArrayList<DBConsumer>();

        configuration = Db4oEmbedded.newConfiguration();

        configureDatabase();

        db = Db4oEmbedded.openFile(configuration, dbFile);
        LOGGER.info("DB init ok");

        //Baaaaah! Evolution ruined our IoC!
        XMLDBFormat.setDb(db);

        db.ext().backup("storm8.bak");
    }

    private void configureDatabase() {
        configuration.common().automaticShutDown(false);

        configuration.common().exceptionsOnNotStorable(true);

        configuration.common().add(new UniqueFieldValueConstraint(Game.class, "id"));
        configuration.common().objectClass(Game.class).cascadeOnDelete(true);
        configuration.common().objectClass(Game.class).cascadeOnUpdate(true);

        configuration.common().add(new UniqueFieldValueConstraint(Topic.class, "topicId"));
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface Provider ---------------------

    public ObjectContainer get() {
        return db;
    }

// -------------------------- OTHER METHODS --------------------------

    public synchronized void shutdown() {
        for (DBConsumer consumer : consumers) {
            consumer.requestShutdown();
        }

        if (db != null && !db.ext().isClosed()) {
            LOGGER.info("DB shutdown");
            db.close();
            XMLDBFormat.setDb(null);
            LOGGER.info("DB shutdown done");
            db = null;
        }
    }

    public void registerConsumer(DBConsumer consumer) {
        consumers.add(consumer);
    }
}

package net.ushkinaz.storm8.configuration;

import com.db4o.ObjectContainer;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import javolution.xml.XMLBinding;
import javolution.xml.XMLObjectReader;
import javolution.xml.stream.XMLStreamException;
import net.ushkinaz.storm8.domain.Configuration;
import net.ushkinaz.storm8.domain.Game;
import net.ushkinaz.storm8.domain.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * @author Dmitry Sidorenko
 */
@Singleton
public class StormConfigurator implements Provider<Configuration> {
// ------------------------------ FIELDS ------------------------------

    @SuppressWarnings({"UnusedDeclaration"})
    private static final Logger LOGGER = LoggerFactory.getLogger(StormConfigurator.class);

    private static final String CONFIG_XML = "configuration.xml";

    private XMLBinding binding;
    private ObjectContainer db;
    private Configuration configuration;

// --------------------------- CONSTRUCTORS ---------------------------

    public StormConfigurator() {
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    @Inject
    public void setBinding(XMLBinding binding) {
        this.binding = binding;
    }

    @Inject
    public void setDb(ObjectContainer db) {
        this.db = db;
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface Provider ---------------------

    @Override
    public Configuration get() {
        synchronized (this) {
            if (configuration == null) {
                configure();
            }
        }
        return configuration;
    }

// -------------------------- OTHER METHODS --------------------------

    protected void configure() {
        try {
            XMLObjectReader reader = XMLObjectReader.newInstance(new FileInputStream(CONFIG_XML));
            assert binding != null;
            reader.setBinding(binding);
            configuration = reader.read("Configuration", Configuration.class);

            for (Game game : configuration.getGames()) {
                db.store(game);
            }
            for (Player player : configuration.getPlayers()) {
                db.store(player);
            }
            db.commit();

            reader.close();
        } catch (XMLStreamException e) {
            LOGGER.error("Error", e);
        } catch (FileNotFoundException e) {
            LOGGER.error("Error", e);
        }
    }
}

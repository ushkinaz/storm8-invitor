package net.ushkinaz.storm8;

import com.db4o.ObjectContainer;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import javolution.xml.XMLBinding;
import javolution.xml.XMLObjectReader;
import javolution.xml.stream.XMLStreamException;
import net.ushkinaz.storm8.domain.Game;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Dmitry Sidorenko
 */
@Singleton
public class StormConfigurator {
    @SuppressWarnings({"UnusedDeclaration"})
    private static final Logger LOGGER = LoggerFactory.getLogger(StormConfigurator.class);

    private static final String GAMES_XML = "games.xml";

    private Map<String, Game> games = new HashMap<String, Game>(10);

    private XMLBinding binding;
    ObjectContainer db;

    @Inject
    public StormConfigurator(XMLBinding binding, ObjectContainer db) {
        this.binding = binding;
        this.db = db;
        configure();
    }

    public Map<String, Game> getGames() {
        return games;
    }

    public Game getGame(String game) {
        return games.get(game.toLowerCase());
    }

    protected void configure() {

        try {
            XMLObjectReader reader = XMLObjectReader.newInstance(new FileInputStream(GAMES_XML));
            reader.setBinding(binding);
            List<Game> gamesList = reader.read("Games");
            reader.close();

            for (Game game : gamesList) {
                games.put(game.getId().toLowerCase(), game);
                db.store(game);
            }
            db.commit();

        } catch (XMLStreamException e) {
            LOGGER.error("XML exception", e);
        } catch (FileNotFoundException e) {
            LOGGER.error("File not found", e);
        }
    }
}

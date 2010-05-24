package net.ushkinaz.storm8;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import javolution.xml.XMLBinding;
import javolution.xml.XMLObjectReader;
import javolution.xml.stream.XMLStreamException;
import net.ushkinaz.storm8.domain.Game;

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
    private static final org.apache.commons.logging.Log LOGGER = org.apache.commons.logging.LogFactory.getLog(StormConfigurator.class);
    private static final String GAMES_XML = "games.xml";

    private Map<String, Game> games = new HashMap<String, Game>(10);

    private XMLBinding binding;

    @Inject
    public StormConfigurator(XMLBinding binding) {
        this.binding = binding;
        configure();
    }

    public Map<String, Game> getGames() {
        return games;
    }

    public Game getGame(String game) {
        return games.get(game);
    }

    protected void configure() {

        try {
            XMLObjectReader reader = XMLObjectReader.newInstance(new FileInputStream(GAMES_XML));
            reader.setBinding(binding);
            List<Game> gamesList = reader.read("Games");
            reader.close();

            for (Game game : gamesList) {
                games.put(game.getName(), game);
            }

        } catch (XMLStreamException e) {
            LOGGER.error(e);
        } catch (FileNotFoundException e) {
            LOGGER.error(e);
        }
    }
}

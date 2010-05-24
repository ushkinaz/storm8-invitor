package net.ushkinaz.storm8;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import javolution.xml.XMLBinding;
import javolution.xml.XMLObjectReader;
import javolution.xml.stream.XMLStreamException;
import net.ushkinaz.storm8.domain.Game;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dmitry Sidorenko
 * @date May 24, 2010
 */
@Singleton
public class StormConfigurator {
    private static final org.apache.commons.logging.Log LOGGER = org.apache.commons.logging.LogFactory.getLog(StormConfigurator.class);
    private static final String GAMES_XML = "games.xml";

    private List<Game> games = new ArrayList<Game>();

    private XMLBinding binding;

    @Inject
    public StormConfigurator(XMLBinding binding) {
        this.binding = binding;
        configure();
    }

    public List<Game> getGames() {
        return games;
    }

    protected void configure() {
        try {
            XMLObjectReader reader = XMLObjectReader.newInstance(new FileInputStream(GAMES_XML));
            reader.setBinding(binding);

            while (reader.hasNext()) {
                Game game = reader.read("Game", Game.class);
                games.add(game);
            }

            reader.close();
        } catch (XMLStreamException e) {
            LOGGER.error(e);
        } catch (FileNotFoundException e) {
            LOGGER.error(e);
        }
    }
}

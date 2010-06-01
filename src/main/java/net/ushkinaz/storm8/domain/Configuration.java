package net.ushkinaz.storm8.domain;

import javolution.xml.XMLFormat;
import javolution.xml.XMLSerializable;
import javolution.xml.stream.XMLStreamException;
import net.ushkinaz.storm8.domain.xml.XMLDBFormat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Date: 31.05.2010
 * Created by Dmitry Sidorenko.
 */
public class Configuration extends Identifiable implements XMLSerializable {
// ------------------------------ FIELDS ------------------------------

    @SuppressWarnings({"UnusedDeclaration"})
    protected static final XMLFormat<Configuration> CONFIGURATION_XML = new XMLDBFormat<Configuration>(Configuration.class) {
        public void write(Configuration configuration, XMLFormat.OutputElement xml) throws XMLStreamException {
            super.write(configuration, xml);
            xml.add(configuration.games, "Games");
            xml.add(configuration.players, "Players");
        }

        public void read(InputElement xml, Configuration configuration) throws XMLStreamException {
            super.read(xml, configuration);
            configuration.games = xml.get("Games");
            configuration.players = xml.get("Players");
            configuration.buildMaps();
        }
    };

    private List<Game> games = new ArrayList<Game>();
    private List<Player> players = new ArrayList<Player>();

    private transient Map<String, Game> gamesMap;
    private transient Map<String, Player> playersMap;

// --------------------------- CONSTRUCTORS ---------------------------

    public Configuration() {
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public List<Game> getGames() {
        return games;
    }

    public List<Player> getPlayers() {
        return players;
    }

// -------------------------- OTHER METHODS --------------------------

    private void buildMaps() {
        gamesMap = new HashMap<String, Game>(10);
        for (Game game : games) {
            gamesMap.put(game.getId(), game);
        }

        playersMap = new HashMap<String, Player>(10);
        for (Player player : players) {
            playersMap.put(player.getId(), player);
        }
    }

    public Game getGame(String name) {
        return gamesMap.get(name);
    }

    public Player getPlayer(String name) {
        return playersMap.get(name);
    }
}

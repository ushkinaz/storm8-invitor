/*
 * Copyright (c) 2010-2010, Dmitry Sidorenko. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

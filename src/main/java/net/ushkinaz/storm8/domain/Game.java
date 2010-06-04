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

import com.db4o.config.annotations.Indexed;
import javolution.xml.XMLFormat;
import javolution.xml.XMLSerializable;
import javolution.xml.stream.XMLStreamException;
import net.ushkinaz.storm8.domain.xml.XMLDBFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Dmitry Sidorenko
 */
public class Game extends Identifiable implements XMLSerializable {
// ------------------------------ FIELDS ------------------------------

    @SuppressWarnings({"UnusedDeclaration"})
    private static final Logger LOGGER = LoggerFactory.getLogger(Game.class);

    private static final long serialVersionUID = 5170559993039725638L;

    @SuppressWarnings({"UnusedDeclaration"})
    protected static final XMLFormat<Game> GAME_XML = new XMLDBFormat<Game>(Game.class) {
        public void write(Game g, XMLFormat.OutputElement xml) throws XMLStreamException {
            super.write(g, xml);
            xml.add(g.name, "name");
            xml.add(g.domain, "domain");
            xml.add(g.clan_uri, "clan_uri");
            xml.add(g.forumId, "forumId");
        }

        public void read(InputElement xml, Game g) throws XMLStreamException {
            super.read(xml, g);
            // If there is an "id" attribute, then this is a reference. Ignore the rest.
            if (xml.getAttribute(REF_ID_ATTRIBUTE, null) == null) {
                g.name = xml.get("name");
                g.domain = xml.get("domain");
                g.clan_uri = xml.get("clan_uri");
                g.forumId = xml.get("forumId");
            }
        }
    };

    @Indexed
    private String name;
    private String domain;
    private String clan_uri;
    private Integer forumId;
    private Map<Integer, Topic> topics = new HashMap<Integer, Topic>(10);
    private Map<Integer, Equipment> equipment = new HashMap<Integer, Equipment>();

// --------------------------- CONSTRUCTORS ---------------------------

    public Game() {
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public Map<Integer, Equipment> getEquipment() {
        if (equipment == null) {
            equipment = new HashMap<Integer, Equipment>();
        }
        return equipment;
    }

    public Integer getForumId() {
        return forumId;
    }

    public void setForumId(Integer forumId) {
        this.forumId = forumId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<Integer, Topic> getTopics() {
        return topics;
    }

    public void setClan_uri(String clan_uri) {
        this.clan_uri = clan_uri;
    }

// ------------------------ CANONICAL METHODS ------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Game)) return false;

        Game game = (Game) o;

        if (clan_uri != null ? !clan_uri.equals(game.clan_uri) : game.clan_uri != null) return false;
        if (domain != null ? !domain.equals(game.domain) : game.domain != null) return false;
        if (name != null ? !name.equals(game.name) : game.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (domain != null ? domain.hashCode() : 0);
        result = 31 * result + (clan_uri != null ? clan_uri.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Game{" +
                "name='" + name + '\'' +
                ", domain='" + domain + '\'' +
                '}';
    }

// -------------------------- OTHER METHODS --------------------------

    public String getClansURL() {
        return "http://" + domain + clan_uri;
    }

    public String getGameURL() {
        return "http://" + domain + "/";
    }
}

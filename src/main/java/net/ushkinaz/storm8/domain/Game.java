package net.ushkinaz.storm8.domain;

import javolution.xml.XMLFormat;
import javolution.xml.XMLSerializable;
import javolution.xml.stream.XMLStreamException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Dmitry Sidorenko
 */
public class Game implements XMLSerializable {
    @SuppressWarnings({"UnusedDeclaration"})
    private static final org.apache.commons.logging.Log LOGGER = org.apache.commons.logging.LogFactory.getLog(Game.class);

    private String name;
    private String domain;
    private String clan_uri;
    private Map<String, String> cookies;
    private List<Topic> topics;

    public Game() {
        cookies = new HashMap<String, String>(10);
        topics = new ArrayList<Topic>();
    }

    public String getName() {
        return name;
    }

    public String getGameURL(){
        return "http://" + domain  + "/";
    }

    public String getClansURL(){
        return "http://" + domain  + clan_uri;
    }

    public String getDomain() {
        return domain;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public List<Topic> getTopics() {
        return topics;
    }

    public void setClan_uri(String clan_uri) {
        this.clan_uri = clan_uri;
    }

    public Map<String, String> getCookies() {
        return cookies;
    }

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

    @SuppressWarnings({"UnusedDeclaration"})
    protected static final XMLFormat<Game> GAME_XML = new XMLFormat<Game>(Game.class) {
        public void write(Game g, XMLFormat.OutputElement xml) throws XMLStreamException {
            xml.setAttribute("name", g.name);
            xml.add(g.domain, "domain");
            xml.add(g.clan_uri, "clan_uri");
            xml.add(g.cookies, "cookies");
            xml.add(g.topics, "topics");
        }

        public void read(InputElement xml, Game g) throws XMLStreamException {
            g.name = xml.getAttribute("name", "");
            g.domain = xml.get("domain");
            g.clan_uri = xml.get("clan_uri");
            g.cookies = xml.get("cookies");
            g.topics = xml.get("topics");
        }
    };

    public String getGameCode() {
        return domain.substring(0, 2);
    }
}

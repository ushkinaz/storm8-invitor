package net.ushkinaz.storm8.domain;

import com.db4o.config.annotations.Indexed;
import javolution.xml.XMLFormat;
import javolution.xml.XMLSerializable;
import javolution.xml.stream.XMLStreamException;
import net.ushkinaz.storm8.domain.xml.XMLDBFormat;

import java.util.*;

/**
 * @author Dmitry Sidorenko
 */
public class Game extends Identifiable implements XMLSerializable {
    @SuppressWarnings({"UnusedDeclaration"})
    private static final org.apache.commons.logging.Log LOGGER = org.apache.commons.logging.LogFactory.getLog(Game.class);
    private static final long serialVersionUID = 5170559993039725638L;

    @Indexed
    private String name;
    private String domain;
    private String clan_uri;
    private Map<String, String> cookies = new HashMap<String, String>(5);
    private Map<Integer, Topic> topics = new HashMap<Integer, Topic>(10);
    private Integer forumId;


    public Game() {
    }

    public Game(String id) {
        super(id);
    }

    public String getName() {
        return name;
    }

    public Integer getForumId() {
        return forumId;
    }

    public void setForumId(Integer forumId) {
        this.forumId = forumId;
    }

    public String getGameURL() {
        return "http://" + domain + "/";
    }

    public String getClansURL() {
        return "http://" + domain + clan_uri;
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

    public Map<Integer, Topic> getTopics() {
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
    protected static final XMLFormat<Game> GAME_XML = new XMLDBFormat<Game>(Game.class) {

        public void write(Game g, XMLFormat.OutputElement xml) throws XMLStreamException {
            super.write(g, xml);
            xml.add(g.name, "name");
            xml.add(g.domain, "domain");
            xml.add(g.clan_uri, "clan_uri");
            xml.add(g.cookies, "cookies");
            xml.add(g.forumId, "forumId");
        }

        public void read(InputElement xml, Game g) throws XMLStreamException {
            super.read(xml, g);
            g.name = xml.get("name");
            g.domain = xml.get("domain");
            g.clan_uri = xml.get("clan_uri");
            g.cookies = xml.get("cookies");
            g.forumId = xml.get("forumId");
        }
    };
}

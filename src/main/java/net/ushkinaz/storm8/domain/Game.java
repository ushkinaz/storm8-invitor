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
 * @date May 24, 2010
 */
public class Game implements XMLSerializable {
    private static final org.apache.commons.logging.Log LOGGER = org.apache.commons.logging.LogFactory.getLog(Game.class);

    private String domain;
    private String url;
    private String clan_uri;
    private Map<String, String> cookies;
    private List<Topic> topics;

    public Game() {
        cookies = new HashMap<String, String>(10);
        topics = new ArrayList<Topic>();
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

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<Topic> getTopics() {
        return topics;
    }

    public String getClan_uri() {
        return clan_uri;
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
        if (!domain.equals(game.domain)) return false;
        if (url != null ? !url.equals(game.url) : game.url != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return domain.hashCode();
    }

    protected static final XMLFormat<Game> GAME_XML = new XMLFormat<Game>(Game.class) {
        public void write(Game g, XMLFormat.OutputElement xml) throws XMLStreamException {
            xml.setAttribute("domain", g.domain);
            xml.add(g.url, "url");
            xml.add(g.clan_uri, "clan_uri");
            xml.add(g.cookies, "cookies");
            xml.add(g.topics, "topics");
        }

        public void read(InputElement xml, Game g) throws XMLStreamException {
            g.domain = xml.getAttribute("domain", "");
            g.url = xml.get("url");
            g.clan_uri = xml.get("clan_uri");
            g.cookies = xml.get("cookies");
            g.topics = xml.get("topics");
        }
    };
}

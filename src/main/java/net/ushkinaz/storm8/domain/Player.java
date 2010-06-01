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
 * Date: 31.05.2010
 * Created by Dmitry Sidorenko.
 */
public class Player extends Identifiable implements XMLSerializable {
// ------------------------------ FIELDS ------------------------------

    @SuppressWarnings({"UnusedDeclaration"})
    private static final Logger LOGGER = LoggerFactory.getLogger(Player.class);
    private static final long serialVersionUID = -1909196133671471773L;

    @SuppressWarnings({"UnusedDeclaration"})
    protected static final XMLFormat<Player> PLAYER_XML = new XMLDBFormat<Player>(Player.class) {
        public void write(Player player, XMLFormat.OutputElement xml) throws XMLStreamException {
            super.write(player, xml);
            xml.setAttribute("name", player.name);
            xml.setAttribute("code", player.code);
            xml.add(player.cookies, "cookies");
            xml.add(player.game, "game");
        }

        public void read(InputElement xml, Player player) throws XMLStreamException {
            super.read(xml, player);
            player.name = xml.getAttribute("name", "");
            player.code = xml.getAttribute("code", "");
            player.cookies = xml.get("cookies");
            player.game = xml.get("game");
        }
    };

    private Game game;
    private String name;
    private String code;
    private Map<String, String> cookies = new HashMap<String, String>(5);

// --------------------------- CONSTRUCTORS ---------------------------

    @SuppressWarnings({"UnusedDeclaration"})
    public Player() {
    }

    public Player(Game game) {
        super();
        this.game = game;
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Map<String, String> getCookies() {
        return cookies;
    }

    public Game getGame() {
        return game;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

// ------------------------ CANONICAL METHODS ------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Player player = (Player) o;

        if (!code.equals(player.code)) return false;
        if (!game.equals(player.game)) return false;
        if (!name.equals(player.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = game.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + code.hashCode();
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Player");
        sb.append("{game=").append(game);
        sb.append(", name='").append(name).append('\'');
        sb.append(", code='").append(code).append('\'');
        sb.append('}');
        return sb.toString();
    }

}

package net.ushkinaz.storm8.domain;
/**
 * Date: 31.05.2010
 * Created by Dmitry Sidorenko.
 */

import javolution.xml.XMLBinding;
import javolution.xml.XMLObjectReader;
import javolution.xml.XMLObjectWriter;
import javolution.xml.stream.XMLStreamException;
import net.ushkinaz.storm8.domain.xml.XMLBinderFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class PlayerTest {
    private static final String PLAYER_XML = "player-test.xml";
    private Player player;
    private Game game;
    private XMLBinding binding;


    @Before
    public void beforeClass() {
        XMLBinderFactory xmlBinderFactory = new XMLBinderFactory();
        binding = xmlBinderFactory.get();

        game = new Game();
        game.setName("Ninja");
        game.setDomain("nl.storm8.com");
        game.setClan_uri("/group.php");

        player = new Player(game);
        player.setCode("XUJDS");
        player.setName("Dmitry");

        player.getCookies().put("ask", "1ec7c54864b2d968f89aa6453b067a9d4bff8a2f");
        player.getCookies().put("st", "2792593%2Cc2e17ffa909fd6d4a6b1bbe219fed884ffddb425%2C1274554582%2C12%2C%2Ca1.54%2C14%2C4%2C10003%2C2010-05-22+11%3A56%3A22%2C%2Cv1_1274554582_5e4680101c1b52f0674b973b4a03b1aaf3043356");    }

    @Test
    public void testSerialization() throws XMLStreamException, FileNotFoundException {
        // Writes the area to a file.
        XMLObjectWriter writer = XMLObjectWriter.newInstance(new FileOutputStream(PLAYER_XML, false));
        writer.setBinding(binding); // Optional.
        writer.setIndentation("\t"); // Optional (use tabulation for indentation).
        writer.write(player, "Player");
        writer.close();

        // Reads the area back
        XMLObjectReader reader = XMLObjectReader.newInstance(new FileInputStream(PLAYER_XML));
        reader.setBinding(binding);
        Player xmlPlayer = reader.read("Player", Player.class);
        reader.close();

        Assert.assertEquals(player, xmlPlayer);
    }
}
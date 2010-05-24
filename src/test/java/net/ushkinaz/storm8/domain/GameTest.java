package net.ushkinaz.storm8.domain;

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

/**
 * @author Dmitry Sidorenko
 * @date May 24, 2010
 */

public class GameTest {
    private Game game;
    private static final String GAMES_XML = "games-test.xml";
    private XMLBinding binding;


    @Before
    public void beforeClass(){
        XMLBinderFactory xmlBinderFactory = new XMLBinderFactory();
        binding = xmlBinderFactory.get();

        game = new Game();
        game.setName("Ninja");
        game.setDomain("nl.storm8.com");
        game.setClan_uri("/group.php");
        game.getCookies().put("ask", "1ec7c54864b2d968f89aa6453b067a9d4bff8a2f");
        game.getCookies().put("st", "2792593%2Cc2e17ffa909fd6d4a6b1bbe219fed884ffddb425%2C1274554582%2C12%2C%2Ca1.54%2C14%2C4%2C10003%2C2010-05-22+11%3A56%3A22%2C%2Cv1_1274554582_5e4680101c1b52f0674b973b4a03b1aaf3043356");

        game.getTopics().add(new Topic(11134));
        game.getTopics().add(new Topic(90434));
    }

    @Test
    public void testSerialization() throws XMLStreamException, FileNotFoundException {
        // Writes the area to a file.
        XMLObjectWriter writer = XMLObjectWriter.newInstance(new FileOutputStream(GAMES_XML, false));
        writer.setBinding(binding); // Optional.
        writer.setIndentation("\t"); // Optional (use tabulation for indentation).
        writer.write(game, "Game", Game.class);
        writer.close();

        // Reads the area back
        XMLObjectReader reader = XMLObjectReader.newInstance(new FileInputStream(GAMES_XML));
        reader.setBinding(binding);
        Game xmlGame = reader.read("Game", Game.class);
        reader.close();

        Assert.assertEquals(game, xmlGame);
    }
}
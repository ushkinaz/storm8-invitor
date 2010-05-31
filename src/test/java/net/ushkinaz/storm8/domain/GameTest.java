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
    public void beforeClass() {
        XMLBinderFactory xmlBinderFactory = new XMLBinderFactory();
        binding = xmlBinderFactory.get();

        game = new Game();
        game.setName("Ninja");
        game.setDomain("nl.storm8.com");
        game.setClan_uri("/group.php");
        game.getTopics().put(11134, new Topic(11134));
        game.getTopics().put(90434, new Topic(90434));
    }

    @Test
    public void testSerialization() throws XMLStreamException, FileNotFoundException {
        // Writes the area to a file.
        XMLObjectWriter writer = XMLObjectWriter.newInstance(new FileOutputStream(GAMES_XML, false));
        writer.setBinding(binding); // Optional.
        writer.setIndentation("\t"); // Optional (use tabulation for indentation).
        writer.write(game, "Game");
        writer.close();

        // Reads the area back
        XMLObjectReader reader = XMLObjectReader.newInstance(new FileInputStream(GAMES_XML));
        reader.setBinding(binding);
        Game xmlGame = reader.read("Game", Game.class);
        reader.close();

        Assert.assertEquals(game, xmlGame);
    }
}
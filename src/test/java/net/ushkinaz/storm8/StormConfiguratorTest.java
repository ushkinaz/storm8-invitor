package net.ushkinaz.storm8;

import net.ushkinaz.storm8.domain.Game;
import net.ushkinaz.storm8.domain.xml.XMLBinderFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * @author Dmitry Sidorenko
 * @date May 24, 2010
 */

public class StormConfiguratorTest {
    private StormConfigurator stormConfigurator;

    @Test
    public void testStormConfigurator() throws Exception {
        stormConfigurator = new StormConfigurator(new XMLBinderFactory().get());

        List<Game> games = stormConfigurator.getGames();

        Assert.assertNotNull(games);
    }
}
package net.ushkinaz.storm8;

import com.google.inject.Guice;
import com.google.inject.Injector;
import net.ushkinaz.storm8.domain.Game;
import net.ushkinaz.storm8.domain.xml.XMLBinderFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

/**
 * @author Dmitry Sidorenko
 * @date May 24, 2010
 */

public class StormConfiguratorTest {
    private StormConfigurator stormConfigurator;

    @Test
    public void testStormConfigurator() throws Exception {
        final Storm8Module module = new Storm8TestModule();
        Injector injector = Guice.createInjector(module);

        stormConfigurator = injector.getInstance(StormConfigurator.class);

        Map<String, Game> games = stormConfigurator.getGames();

        Assert.assertNotNull(games);
    }

    @Test
    public void testConfigure() throws Exception {
        stormConfigurator.db.query(Game.class);
    }
}
package net.ushkinaz.storm8;

import com.google.inject.Guice;
import com.google.inject.Injector;
import net.ushkinaz.storm8.domain.Game;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

/**
 * @author Dmitry Sidorenko
 * @date May 24, 2010
 */

public class StormConfiguratorTest {
    private StormConfigurator stormConfigurator;
    private Storm8Module module;

    @Before
    public void setup(){
        module = new Storm8TestModule();
        Injector injector = Guice.createInjector(module);

        stormConfigurator = injector.getInstance(StormConfigurator.class);
    }

    @Before
    public void shutdown(){
        module.shutdown();
    }


    @Test
    public void testStormConfigurator() throws Exception {

        Map<String, Game> games = stormConfigurator.getGames();

        Assert.assertNotNull(games);
    }
}
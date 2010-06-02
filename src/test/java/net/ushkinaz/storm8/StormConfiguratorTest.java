package net.ushkinaz.storm8;

import com.google.inject.Guice;
import com.google.inject.Injector;
import net.ushkinaz.storm8.configuration.StormConfigurator;
import net.ushkinaz.storm8.domain.Configuration;
import net.ushkinaz.storm8.guice.Storm8Module;
import net.ushkinaz.storm8.guice.Storm8TestModule;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Dmitry Sidorenko
 * @date May 24, 2010
 */

public class StormConfiguratorTest {
    private StormConfigurator stormConfigurator;
    private Storm8Module module;

    @Before
    public void setup() {
        module = new Storm8TestModule();
        Injector injector = Guice.createInjector(module);

        stormConfigurator = injector.getInstance(StormConfigurator.class);
    }

    @Before
    public void shutdown() {
        module.shutdown();
    }


    @Test
    public void testStormConfigurator() throws Exception {

        Configuration configuration = stormConfigurator.get();

        Assert.assertNotNull(configuration);
    }
}
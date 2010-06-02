package net.ushkinaz.storm8;

import com.google.inject.Guice;
import com.google.inject.Injector;
import net.ushkinaz.storm8.guice.Storm8Module;
import net.ushkinaz.storm8.guice.Storm8TestModule;
import org.junit.After;
import org.junit.Before;

public abstract class GuiceAbstractTest {
    protected Storm8Module module;
    protected Injector injector;

    public GuiceAbstractTest() {
    }

    @Before
    public void setupGuice() {
        module = new Storm8TestModule();
        injector = Guice.createInjector(module);
    }

    @After
    public void shutdownGuice() {
        module.shutdown();
    }
}
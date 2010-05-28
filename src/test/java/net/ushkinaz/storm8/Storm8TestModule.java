package net.ushkinaz.storm8;

import net.ushkinaz.storm8.dao.DB4OProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * @author Dmitry Sidorenko
 * @date May 25, 2010
 */
public class Storm8TestModule extends Storm8Module {
    @SuppressWarnings({"UnusedDeclaration"})
    private static final Logger LOGGER = LoggerFactory.getLogger(Storm8TestModule.class);

    public Storm8TestModule() {
        super("test.db");
    }

    /**
     * Does the same thing as super, but deletes database first.
     *
     * @return
     */
    @Override
    protected DB4OProvider createDB4OProvider() {
        new File(dbFile).delete();
        return super.createDB4OProvider();
    }

    @Override
    public void shutdown() {
        super.shutdown();
        //Cleanup
        new File(dbFile).delete();
    }
}

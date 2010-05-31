package net.ushkinaz.storm8.dao;
/**
 * @author Dmitry Sidorenko
 * @date May 25, 2010
 */

import com.db4o.ObjectContainer;
import com.db4o.constraints.UniqueFieldValueConstraintViolationException;
import net.ushkinaz.storm8.GuiceAbstractTest;
import net.ushkinaz.storm8.domain.ClanInvite;
import net.ushkinaz.storm8.domain.Game;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DB4OProviderTest extends GuiceAbstractTest{
    private DB4OProvider db4OProvider;
    private ObjectContainer db;


    @Before
    public void setup() {
        db4OProvider = module.getDb4oOProvider();
        db = injector.getInstance(ObjectContainer.class);
    }

    @Test
    public void testDoubleClan() throws Exception {

        try {
            Game game;
            game = new Game("one");

            db.store(game);

            game = new Game("one");
            db.store(game);

            db.commit();
            Assert.fail("Should throw an exception");
        } catch (UniqueFieldValueConstraintViolationException e) {
            db.rollback();
        }

        List<ClanInvite> set = db.queryByExample(Game.class);
        assertThat(set.size(), is(0));
    }

    @Test
    public void testGetDb() throws Exception {
        Assert.assertNotNull(db4OProvider.get());
    }

    @Test
    public void testShutdown() throws Exception {
        db4OProvider.shutdown();
        Assert.assertNull(db4OProvider.get());
    }

    /**
     * No exceptions should be thrown
     *
     * @throws Exception
     */
    @Test
    public void testDoubleShutdown() throws Exception {
        db4OProvider.shutdown();
        db4OProvider.shutdown();
    }

}
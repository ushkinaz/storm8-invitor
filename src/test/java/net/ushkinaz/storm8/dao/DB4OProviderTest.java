package net.ushkinaz.storm8.dao;
/**
 * @author Dmitry Sidorenko
 * @date May 25, 2010
 */

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.constraints.UniqueFieldValueConstraintViolationException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import net.ushkinaz.storm8.Storm8Module;
import net.ushkinaz.storm8.Storm8TestModule;
import net.ushkinaz.storm8.domain.ClanInvite;
import net.ushkinaz.storm8.domain.ClanInviteStatus;
import net.ushkinaz.storm8.domain.Game;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DB4OProviderTest {
    private DB4OProvider db4OProvider;
    private ObjectContainer db;


    @Before
    public void setup() {
        final Storm8Module module = new Storm8TestModule();
        Injector injector = Guice.createInjector(module);

        db4OProvider = module.getDb4oOProvider();

        db = injector.getInstance(ObjectContainer.class);
    }

    @After
    public void shutdown() {
        if (!db.ext().isClosed()) {
            db.rollback();
        }
        db.close();
    }

    @Test(expected = UniqueFieldValueConstraintViolationException.class)
    public void testDoubleClan() throws Exception {
        ClanInvite clanInvite;
        clanInvite = new ClanInvite();
        clanInvite.setCode("S223s");
        clanInvite.setGame(new Game());
        clanInvite.setStatus(ClanInviteStatus.ACCEPTED);

        db.store(clanInvite);

        ClanInvite clanInvite1;
        clanInvite1 = new ClanInvite();
        clanInvite1.setCode("S223s");
        clanInvite1.setGame(new Game());
        clanInvite1.setStatus(ClanInviteStatus.ACCEPTED);

        db.store(clanInvite1);

        db.commit();
        List<ClanInvite> set = db.queryByExample(ClanInvite.class);
        assertThat(set.size(), is(1));
    }

    @Test
    public void testPlay() throws Exception {
        final Storm8Module module = new Storm8Module("storm8.db");
        Injector injector = Guice.createInjector(module);

        db4OProvider = module.getDb4oOProvider();

        db = injector.getInstance(ObjectContainer.class);

        ObjectSet<Game> games = db.query(Game.class);
        ObjectSet<ClanInvite> invites = db.query(ClanInvite.class);

        Assert.assertNotNull(invites);
        Assert.assertNotNull(games);
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

    @Test
    public void testDoubleShutdown() throws Exception {
        db4OProvider.shutdown();
        db4OProvider.shutdown();
    }

}
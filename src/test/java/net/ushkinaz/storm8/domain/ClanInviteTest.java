package net.ushkinaz.storm8.domain;

import com.db4o.ObjectContainer;
import com.google.inject.Guice;
import com.google.inject.Injector;
import net.ushkinaz.storm8.Storm8Module;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Date: 25.05.2010
 * Created by Dmitry Sidorenko.
 */
public class ClanInviteTest {
    private ObjectContainer connector;

    @Before
    public void setUp() {
        Injector injector = Guice.createInjector(new Storm8Module());

        connector = injector.getInstance(ObjectContainer.class);
    }

    @After
    public void shutdown() {
    }

    @Test
    public void testEquals() {
        ClanInvite clanInvite;
        clanInvite = new ClanInvite();
        clanInvite.setCode("S223s");
        clanInvite.setGame("NL");
        clanInvite.setStatus(ClanInviteStatus.ACCEPTED);

        ClanInvite clanInvite1;
        clanInvite1 = new ClanInvite();
        clanInvite1.setCode("S223s");
        clanInvite1.setGame("NL");
        clanInvite1.setStatus(ClanInviteStatus.ACCEPTED);

        Assert.assertEquals(clanInvite, clanInvite1);
        Assert.assertNotSame(clanInvite, clanInvite1);

    }
}

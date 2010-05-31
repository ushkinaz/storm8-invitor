package net.ushkinaz.storm8.domain;

import net.ushkinaz.storm8.GuiceAbstractTest;
import org.junit.Assert;
import org.junit.Test;

/**
 * Date: 25.05.2010
 * Created by Dmitry Sidorenko.
 */
public class ClanInviteTest extends GuiceAbstractTest {

    @Test
    public void testEquals() {
        ClanInvite clanInvite;
        clanInvite = new ClanInvite();
        clanInvite.setCode("S223s");
        final Game game = new Game();
        clanInvite.setGame(game);
        clanInvite.setStatus(ClanInviteStatus.ACCEPTED);

        ClanInvite clanInvite1;
        clanInvite1 = new ClanInvite();
        clanInvite1.setCode("S223s");
        clanInvite1.setGame(game);
        clanInvite1.setStatus(ClanInviteStatus.ACCEPTED);

        Assert.assertEquals(clanInvite, clanInvite1);
        Assert.assertNotSame(clanInvite, clanInvite1);

    }
}

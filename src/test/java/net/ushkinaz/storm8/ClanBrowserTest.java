package net.ushkinaz.storm8;

import net.ushkinaz.storm8.domain.Configuration;
import net.ushkinaz.storm8.domain.Victim;
import net.ushkinaz.storm8.explorer.ClanBrowser;
import net.ushkinaz.storm8.explorer.ProfileVisitor;
import net.ushkinaz.storm8.http.PageExpiredException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Dmitry Sidorenko
 * @date Jun 1, 2010
 */
public class ClanBrowserTest extends GuiceAbstractTest {
    private ClanBrowser clanBrowser;
    private Configuration configuration;

    @Before
    public void setUp() throws Exception {
        clanBrowser = injector.getInstance(ClanBrowser.class);
        configuration = injector.getInstance(Configuration.class);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testVisitClanMembers() throws Exception {
        clanBrowser.setPlayer(configuration.getPlayer("ush-ninja"));
        clanBrowser.visitClanMembers(new ProfileVisitor() {
            @Override
            public void visitProfile(Victim victim, String profileHTML) throws PageExpiredException {
            }
        });
    }
}

package net.ushkinaz.storm8.domain;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.config.EmbeddedConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * Date: 25.05.2010
 * Created by Dmitry Sidorenko.
 */
public class ClanInviteTest {
    @Before
    public void setUp() throws Exception {

        EmbeddedConfiguration configuration = Db4oEmbedded.newConfiguration();
        configuration.common().objectClass(ClanInvite.class).objectField("code").indexed(true);


        ObjectContainer db = Db4oEmbedded.openFile(configuration, "storm8.db");
        ClanInvite clanInvite = new ClanInvite();
        clanInvite.setCode("S223s");
        clanInvite.setGame("NL");
        clanInvite.setStatus(ClanInviteStatus.ACCEPTED);
        db.store(clanInvite);

        List<ClanInvite> set = db.queryByExample(ClanInvite.class);
        set.hashCode();
    }

    @Test
    public void testME() {

    }

    @After
    public void tearDown() throws Exception {

    }
}

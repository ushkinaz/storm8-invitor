package net.ushkinaz.storm8.guice;

import net.ushkinaz.storm8.domain.Player;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Dmitry Sidorenko
 * @date Jun 2, 2010
 */
public class PlayerProviderTest {
    @SuppressWarnings({"UnusedDeclaration"})
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerProviderTest.class);
    private PlayerProvider provider;

    @Before
    public void setUp() throws Exception {
        provider = new PlayerProvider();
    }

    @Test
    public void testSetPlayer() throws Exception {
        for(int i=0; i< 10; i++){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Player player = new Player();
                    provider.setPlayer(player);
                    Assert.assertSame(player, provider.get());
                }
            }).start();

        }

    }
}

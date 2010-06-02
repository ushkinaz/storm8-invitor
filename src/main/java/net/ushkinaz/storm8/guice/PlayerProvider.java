package net.ushkinaz.storm8.guice;

import com.google.inject.Provider;
import com.google.inject.Singleton;
import net.ushkinaz.storm8.domain.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hack code.
 * We need to inject players at different places.
 * The problem is that we need to inject different players each time.
 * This class uses ThreadLocal to store Players.
 *
 * @author Dmitry Sidorenko
 */
@Singleton
public class PlayerProvider implements Provider<Player> {
    @SuppressWarnings({"UnusedDeclaration"})
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerProvider.class);

    ThreadLocal<Player> player = new ThreadLocal<Player>();


    public void setPlayer(Player player) {
        this.player.set(player);
    }

    @Override
    public Player get() {
        return player.get();
    }
}

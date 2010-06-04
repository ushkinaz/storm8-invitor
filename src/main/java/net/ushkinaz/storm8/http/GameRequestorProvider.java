package net.ushkinaz.storm8.http;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import net.ushkinaz.storm8.domain.Player;
import net.ushkinaz.storm8.guice.PlayerProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Dmitry Sidorenko
 * @date Jun 1, 2010
 */
@Singleton
public class GameRequestorProvider implements Provider<GameRequestor> {
// ------------------------------ FIELDS ------------------------------

    @SuppressWarnings({"UnusedDeclaration"})
    private static final Logger LOGGER = LoggerFactory.getLogger(GameRequestorProvider.class);
    private HttpClientProvider clientProvider;

    private final Map<Player, GameRequestor> requestors = new HashMap<Player, GameRequestor>();
    private PlayerProvider playerProvider;

// --------------------------- CONSTRUCTORS ---------------------------

    @Inject
    public GameRequestorProvider(HttpClientProvider clientProvider, PlayerProvider playerProvider) {
        this.clientProvider = clientProvider;
        this.playerProvider = playerProvider;
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface Provider ---------------------

    @Override
    public GameRequestor get() {
        GameRequestor gameRequestor;
        synchronized (requestors) {
            if (!requestors.containsKey(playerProvider.get())) {
                gameRequestor = new GameRequestor(playerProvider.get());
                gameRequestor.setClientProvider(clientProvider);
                requestors.put(playerProvider.get(), gameRequestor);
            }
        }
        return requestors.get(playerProvider.get());
    }
}

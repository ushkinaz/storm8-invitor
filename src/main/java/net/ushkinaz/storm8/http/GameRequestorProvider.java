package net.ushkinaz.storm8.http;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.ushkinaz.storm8.domain.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Dmitry Sidorenko
 * @date Jun 1, 2010
 */
@Singleton
public class GameRequestorProvider {
// ------------------------------ FIELDS ------------------------------

    @SuppressWarnings({"UnusedDeclaration"})
    private static final Logger LOGGER = LoggerFactory.getLogger(GameRequestorProvider.class);
    private HttpClientProvider clientProvider;

    private final Map<Player, GameRequestor> requestors = new HashMap<Player, GameRequestor>();

// --------------------------- CONSTRUCTORS ---------------------------

    public GameRequestorProvider() {
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    @Inject
    public void setClientProvider(HttpClientProvider clientProvider) {
        this.clientProvider = clientProvider;
    }

// -------------------------- OTHER METHODS --------------------------

    public GameRequestor getRequestor(Player player) {
        GameRequestor gameRequestor;
        synchronized (requestors) {
            if (!requestors.containsKey(player)) {
                gameRequestor = new GameRequestor(player);
                gameRequestor.setClientProvider(clientProvider);
                requestors.put(player, gameRequestor);
            }
        }

        gameRequestor = requestors.get(player);
        return gameRequestor;
    }
}

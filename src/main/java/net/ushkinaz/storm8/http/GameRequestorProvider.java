package net.ushkinaz.storm8.http;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.ushkinaz.storm8.domain.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        GameRequestor gameRequestor = new GameRequestor(player);
        gameRequestor.setClientProvider(clientProvider);
        return gameRequestor;
    }
}

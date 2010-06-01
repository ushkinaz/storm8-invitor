package net.ushkinaz.storm8.http;

import com.google.inject.Inject;
import net.ushkinaz.storm8.domain.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Dmitry Sidorenko
 * @date Jun 1, 2010
 */
public class GameRequestorProvider {
    @SuppressWarnings({"UnusedDeclaration"})
    private static final Logger LOGGER = LoggerFactory.getLogger(GameRequestorProvider.class);
    private HttpClientProvider clientProvider;

    @Inject
    private GameRequestorProvider(HttpClientProvider clientProvider) {
        this.clientProvider = clientProvider;
    }

    public GameRequestor getRequestor(Player player) {
        return new GameRequestor(player, clientProvider);
    }
}

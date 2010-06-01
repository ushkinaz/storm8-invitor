package net.ushkinaz.storm8.invite;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.ushkinaz.storm8.dao.ClanDao;
import net.ushkinaz.storm8.domain.ClanInvite;
import net.ushkinaz.storm8.domain.ClanInviteStatus;
import net.ushkinaz.storm8.domain.Player;
import net.ushkinaz.storm8.http.GameRequestor;
import net.ushkinaz.storm8.http.HttpClientProvider;
import net.ushkinaz.storm8.http.ServerWorkflowException;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Date: 23.05.2010
 * Created by Dmitry Sidorenko.
 */
@Singleton
public class InviteService {
    private static final Logger LOGGER = getLogger(InviteService.class);

    private InviteParser inviteParser;
    private ClanDao clanDao;
    private HttpClientProvider httpClientProvider;
    private final ThreadPoolExecutor threadPoolExecutor;

    @Inject
    private InviteService(ClanDao clanDao, InviteParser inviteParser, HttpClientProvider httpClientProvider) throws Exception {
        this.clanDao = clanDao;
        this.inviteParser = inviteParser;
        this.httpClientProvider = httpClientProvider;
        threadPoolExecutor = new ThreadPoolExecutor(0, 10, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
    }

    /**
     * Invites clans for given game.
     * All clan codes should be in DB by that time.
     *
     * @param player members will be invited to his clan
     */
    public void invite(final Player player) {
        LOGGER.debug(">> invite");
        final GameRequestor gameRequestor = new GameRequestor(player, httpClientProvider);
        Collection<ClanInvite> invites = clanDao.getByStatus(player.getGame(), ClanInviteStatus.DIGGED);

        final int[] count = {invites.size()};
        for (final ClanInvite invite : invites) {
            threadPoolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        invite(player, invite, gameRequestor);
                    } catch (ServerWorkflowException e) {
                        //todo: need to re throw
                        LOGGER.error("Error", e);
                    } finally {
                        count[0]--;
                        if (count[0] % 50 == 0) {
                            LOGGER.info("Invites to process: " + count[0]);
                        }
                    }
                }
            });
        }
        LOGGER.debug("<< invite");
    }

    private void invite(Player player, ClanInvite clanInvite, GameRequestor gameRequestor) throws ServerWorkflowException {
        if (clanInvite.isInvited()) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Skipping:" + clanInvite);
            }
            return;
        }

        try {
            String responseBody = gameRequestor.postRequest(player.getGame().getClansURL(), new InviteClanPostBodyFactory(clanInvite));

            inviteParser.parseResult(responseBody, clanInvite);
            clanDao.updateClanInvite(clanInvite);
        } catch (ServerWorkflowException e) {
            // Bad thing happened
            LOGGER.error("Bad thing happened", e);
            threadPoolExecutor.shutdownNow();
        } catch (IOException e) {
            LOGGER.error("IO error: ", e);
        }
    }
}

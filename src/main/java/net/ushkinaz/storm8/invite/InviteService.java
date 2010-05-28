package net.ushkinaz.storm8.invite;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.ushkinaz.storm8.dao.ClanDao;
import net.ushkinaz.storm8.domain.ClanInvite;
import net.ushkinaz.storm8.domain.ClanInviteStatus;
import net.ushkinaz.storm8.domain.Game;
import net.ushkinaz.storm8.http.GameRequestor;
import net.ushkinaz.storm8.http.HttpClientProvider;
import net.ushkinaz.storm8.http.ServerWorkflowException;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
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

    @Inject
    public InviteService(ClanDao clanDao, InviteParser inviteParser,  HttpClientProvider httpClientProvider) throws Exception {
        this.clanDao = clanDao;
        this.inviteParser = inviteParser;
        this.httpClientProvider = httpClientProvider;
    }

    /**
     * Invites clans for given game.
     * All clan codes should be in DB by that time.
     *
     * @param game game to use invitations
     */
    public void invite(Game game) {
        final GameRequestor gameRequestor = new GameRequestor(game, httpClientProvider);
        Collection<ClanInvite> invites = clanDao.getByStatus(gameRequestor.getGame(), ClanInviteStatus.DIGGED);

        ExecutorService executorService = new ThreadPoolExecutor(5, 10, 120, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(1000));
        for (final ClanInvite invite : invites) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        invite(gameRequestor, invite);
                    } catch (ServerWorkflowException e) {
                        //todo: need to re throw
                        LOGGER.error("Error", e);
                    }
                }
            });
        }
        executorService.shutdown();
        try {
            executorService.awaitTermination(60, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            LOGGER.error("Error", e);
        }
    }

    private void invite(GameRequestor gameRequestor, ClanInvite clanInvite) throws ServerWorkflowException {
        if (clanInvite.isInvited()) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Skipping:" + clanInvite);
            }
            return;
        }

        clanDao.updateClanInvite(clanInvite);
        try {
            String responseBody = gameRequestor.postRequest(gameRequestor.getGame().getClansURL(), new InviteClanPostBodyFactory(clanInvite));

            inviteParser.parseResult(responseBody, clanInvite);
        } catch (IOException e) {
            LOGGER.error("IO error: ", e);
        }
    }
}

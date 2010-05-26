package net.ushkinaz.storm8.invite;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.ushkinaz.storm8.dao.ClanDao;
import net.ushkinaz.storm8.domain.ClanInvite;
import net.ushkinaz.storm8.domain.ClanInviteStatus;
import net.ushkinaz.storm8.domain.Game;
import net.ushkinaz.storm8.http.GameRequestor;
import net.ushkinaz.storm8.http.ServerWorkflowException;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.Collection;

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

    @Inject
    public InviteService(ClanDao clanDao, InviteParser inviteParser) throws Exception {
        this.clanDao = clanDao;
        this.inviteParser = inviteParser;
    }

    /**
     * Invites clans for given game.
     * All clan codes should be in DB by that time.
     *
     * @param game game to use invitations
     * @throws IOException an exception
     */
    public void invite(Game game) throws ServerWorkflowException {
        GameRequestor gameRequestor = new GameRequestor(game);
        goThroughDB(gameRequestor);
    }

    private void goThroughDB(GameRequestor gameRequestor) throws ServerWorkflowException {
        Collection<ClanInvite> invites = clanDao.getByStatus(gameRequestor.getGame(), ClanInviteStatus.NOT_FOUND);

        for (ClanInvite invite : invites) {
            invite(gameRequestor, invite);
        }
    }

    // String clanCode, GameRequestor gameRequestor

    private void invite(GameRequestor gameRequestor, ClanInvite clanInvite) throws ServerWorkflowException {
        if (clanInvite.isInvited()) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Skipping:" + clanInvite);
            }
            return;
        }

        clanDao.insertNewClanInvite(clanInvite);
        try {
            String responseBody = gameRequestor.postRequest(gameRequestor.getGame().getClansURL(), new InviteClanPostBodyFactory(clanInvite));

            inviteParser.parseResult(responseBody, clanInvite, gameRequestor.getGame());
        } catch (IOException e) {
            LOGGER.error("IO error: ", e);
        }
    }
}

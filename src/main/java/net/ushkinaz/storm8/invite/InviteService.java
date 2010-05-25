package net.ushkinaz.storm8.invite;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.ushkinaz.storm8.dao.ClanDao;
import net.ushkinaz.storm8.domain.ClanInviteStatus;
import net.ushkinaz.storm8.domain.Game;
import net.ushkinaz.storm8.http.GameRequestor;
import org.slf4j.Logger;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

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
    public void invite(Game game) throws IOException {
        GameRequestor gameRequestor = new GameRequestor(game);
        goThroughDB(gameRequestor);
    }

    private void goThroughDB(GameRequestor gameRequestor) throws IOException {
        ResultSet set = clanDao.getByStatus(ClanInviteStatus.NOT_FOUND, gameRequestor.getGame().getGameCode());
        try {
            while (set.next()) {
                try {
                    String code = set.getString(1);
                    invite(code, gameRequestor);
                } catch (SQLException e) {
                    LOGGER.error("Error", e);
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error", e);
        } finally {
            try {
                set.close();
            } catch (SQLException e) {
                LOGGER.error("Error", e);
            }
        }
    }

    private void invite(String clanCode, GameRequestor gameRequestor) throws IOException {
        if (clanDao.isInvited(clanCode, gameRequestor.getGame().getGameCode())) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Skipping:" + clanCode);
            }
            return;
        }

        clanDao.insertNewClan(clanCode, gameRequestor.getGame().getGameCode());
        String responseBody = gameRequestor.postRequest(gameRequestor.getGame().getClansURL(), new InviteClanPostBodyFactory(clanCode));

        inviteParser.parseResult(responseBody, clanCode, gameRequestor.getGame().getGameCode());
    }
}

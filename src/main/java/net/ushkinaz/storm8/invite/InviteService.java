/*
 * Copyright (c) 2010-2010, Dmitry Sidorenko. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ushkinaz.storm8.invite;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.ushkinaz.storm8.dao.ClanDao;
import net.ushkinaz.storm8.domain.ClanInvite;
import net.ushkinaz.storm8.domain.ClanInviteStatus;
import net.ushkinaz.storm8.domain.Player;
import net.ushkinaz.storm8.http.GameRequestor;
import net.ushkinaz.storm8.http.ServerWorkflowException;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Date: 23.05.2010
 * Created by Dmitry Sidorenko.
 */
@Singleton
public class InviteService {
// ------------------------------ FIELDS ------------------------------

    private static final Logger LOGGER = getLogger(InviteService.class);

    private final ThreadPoolExecutor threadPoolExecutor;
    private InviteParser inviteParser;
    private ClanDao clanDao;
    private GameRequestor gameRequestor;
    private int invitorId;

// --------------------------- CONSTRUCTORS ---------------------------

    @Inject
    public InviteService(InviteParser inviteParser, ClanDao clanDao, GameRequestor gameRequestor) {
        threadPoolExecutor = new ThreadPoolExecutor(0, 5, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new InvitorThreadFactory());
        this.inviteParser = inviteParser;
        this.clanDao = clanDao;
        this.gameRequestor = gameRequestor;
    }

// -------------------------- OTHER METHODS --------------------------

    /**
     * Invites clans for given game.
     * All clan codes should be in DB by that time.
     *
     * @param player members will be invited to his clan
     */
    public void invite(final Player player) {
        LOGGER.debug(">> invite");
        Collection<ClanInvite> invites = clanDao.getByStatus(player.getGame(), ClanInviteStatus.DIGGED);

        final int[] count = {invites.size()};
        for (final ClanInvite invite : invites) {
            if (invite.isInvited()) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Skipping:" + invite);
                }
                continue;
            }
            threadPoolExecutor.execute(new InviteClanAction(player, invite, count));
        }
        LOGGER.info(invites.size() + " invitation jobs are dispatched.");
        LOGGER.debug("<< invite");
    }

    private void invite(Player player, ClanInvite clanInvite, GameRequestor gameRequestor) throws ServerWorkflowException {
        try {
            String responseBody = gameRequestor.postRequest(player.getGame().getClansURL(), new InviteClanPostBodyFactory(clanInvite));

            inviteParser.parseResult(responseBody, clanInvite);
            clanDao.updateClanInvite(clanInvite);
        } catch (ServerWorkflowException e) {
            // Bad thing happened
            LOGGER.error("Bad thing happened", e);
            threadPoolExecutor.shutdownNow();
        }
    }

    public void waitForInvitesToFinish() {
        threadPoolExecutor.shutdown();
    }

    private class InvitorThreadFactory implements ThreadFactory {
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "Invitor " + invitorId++);
        }
    }

    private class InviteClanAction implements Runnable {
        private final Player player;
        private final ClanInvite invite;
        private final int[] count;

        public InviteClanAction(Player player, ClanInvite invite, int[] count) {
            this.player = player;
            this.invite = invite;
            this.count = count;
        }

        @Override
        public void run() {
            try {
                invite(player, invite, gameRequestor);
            } catch (ServerWorkflowException e) {
                //todo: need to re throw
                LOGGER.error("Error inviting clan", e);
            } finally {
                count[0]--;
                if (count[0] % 10 == 0) {
                    LOGGER.info("Invites to process: " + count[0]);
                }
            }
        }
    }
}

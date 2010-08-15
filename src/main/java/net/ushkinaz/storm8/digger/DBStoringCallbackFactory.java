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

package net.ushkinaz.storm8.digger;

import com.db4o.ObjectContainer;
import com.db4o.query.Query;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.ushkinaz.storm8.dao.DB4OProvider;
import net.ushkinaz.storm8.dao.DBConsumer;
import net.ushkinaz.storm8.domain.ClanInvite;
import net.ushkinaz.storm8.domain.ClanInviteSource;
import net.ushkinaz.storm8.domain.ClanInviteStatus;
import net.ushkinaz.storm8.domain.Game;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Dmitry Sidorenko
 */
@Singleton
public class DBStoringCallbackFactory implements DBConsumer {
// ------------------------------ FIELDS ------------------------------

    @SuppressWarnings({"UnusedDeclaration"})
    private static final Logger LOGGER = LoggerFactory.getLogger(DBStoringCallbackFactory.class);
    private BlockingQueue<ClanInvite> clanInvites;

    private ObjectContainer db;
    private Thread workerThread;
    private InvitesConsumer consumer;
    private boolean shutdownRequested;

// --------------------------- CONSTRUCTORS ---------------------------

    @Inject
    public DBStoringCallbackFactory(DB4OProvider db4OProvider, ObjectContainer db) {
        this.db = db;
        clanInvites = new LinkedBlockingQueue<ClanInvite>();
        consumer = new InvitesConsumer(clanInvites);
        workerThread = new Thread(consumer);
        workerThread.setDaemon(true);
        workerThread.setName("Storing invites");
        workerThread.start();

        db4OProvider.registerConsumer(this);
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface DBConsumer ---------------------

    @Override
    public void shutdownPending() {
        LOGGER.debug(">> shutdownPending");
        shutdownRequested = true;
        consumer.finalizeQueue();
        workerThread.interrupt();
        LOGGER.debug("<< shutdownPending");
    }

// -------------------------- OTHER METHODS --------------------------

    public PageDigger.CodesDiggerCallback get(final Game game, final ClanInviteSource inviteSource) {
        return new PageDigger.CodesDiggerCallback() {
            @Override
            public void codeFound(String code) {
                ClanInvite clanInvite = new ClanInvite(code, game);
                clanInvite.setStatus(ClanInviteStatus.DIGGED);
                clanInvite.setInviteSource(inviteSource);
                if (shutdownRequested) {
                    throw new IllegalStateException("Shutdown requested");
                } else {
                    clanInvites.add(clanInvite);
                }
            }
        };
    }

// -------------------------- INNER CLASSES --------------------------

    class InvitesConsumer implements Runnable {
        private final BlockingQueue<ClanInvite> queue;

        InvitesConsumer(BlockingQueue<ClanInvite> blockingQueue) {
            queue = blockingQueue;
        }

        public void run() {
            try {
                while (true) {
                    consume(queue.take());
                }
            } catch (InterruptedException ex) {
                //Ignore exception
            }
        }

        private synchronized void finalizeQueue() {
            LOGGER.info(">> finalizeQueue");
            for (ClanInvite clanInvite : queue) {
                consume(clanInvite);
            }
            LOGGER.info("<< finalizeQueue");
        }

        void consume(ClanInvite clanInvite) {
            if (db.ext().isClosed()) {
                LOGGER.debug("Will I die, daddy?");
                throw new IllegalStateException("Will I die, daddy?");
//                return;
            }
            Query query = db.query();
            query.constrain(ClanInvite.class);
            query.descend("game").constrain(clanInvite.getGame());
            query.descend("code").constrain(clanInvite.getCode());

            if (query.execute().size() == 0) {
                db.store(clanInvite);
                db.commit();
                LOGGER.info("Stored invite: " + clanInvite);
            }
        }
    }
}

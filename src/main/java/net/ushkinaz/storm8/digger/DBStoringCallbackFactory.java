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
    @SuppressWarnings({"UnusedDeclaration"})
    private static final Logger LOGGER = LoggerFactory.getLogger(DBStoringCallbackFactory.class);
// ------------------------------ FIELDS ------------------------------

    private BlockingQueue<ClanInvite> clanInvites;

    private ObjectContainer db;
    private Thread workerThread;
    private InvitesConsumer consumer;
    private boolean shutdownRequested;

// --------------------------- CONSTRUCTORS ---------------------------

    public DBStoringCallbackFactory() {
        clanInvites = new LinkedBlockingQueue<ClanInvite>();
        consumer = new InvitesConsumer(clanInvites);
        workerThread = new Thread(consumer);
        workerThread.setDaemon(true);
        workerThread.setName("Storing invites");
        workerThread.start();
    }

    @Inject
    public void setDB4OProvider(DB4OProvider db4OProvider) {
        db4OProvider.registerConsumer(this);
    }
// --------------------- GETTER / SETTER METHODS ---------------------

    @Inject
    public void setDb(ObjectContainer db) {
        this.db = db;
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
                }else{
                    clanInvites.add(clanInvite);
                }

            }
        };
    }

    @Override
    public void requestShutdown() {
        LOGGER.debug(">> requestShutdown");
        shutdownRequested = true;
        consumer.finalizeQueue();
        workerThread.interrupt();
        LOGGER.debug("<< requestShutdown");
    }

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
                return;
            }
            Query query = db.query();
            query.constrain(ClanInvite.class);
            query.descend("game").constrain(clanInvite.getGame());
            query.descend("code").constrain(clanInvite.getCode());

            if (query.execute().size() == 0) {
                db.store(clanInvite);
                db.commit();
            }
        }
    }
}

package net.ushkinaz.storm8.forum;

import com.db4o.ObjectContainer;
import com.google.inject.Inject;
import net.ushkinaz.storm8.domain.ClanInvite;
import net.ushkinaz.storm8.domain.ClanInviteStatus;
import net.ushkinaz.storm8.domain.Game;
import net.ushkinaz.storm8.domain.Topic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

public class ForumCodesDigger implements CodesDigger {
    private static final Logger LOGGER = LoggerFactory.getLogger(ForumCodesDigger.class);

    private AnalyzeTopicService topicService;
    private AnalyzeForumService forumService;
    private ObjectContainer db;

    @Inject
    public ForumCodesDigger(AnalyzeTopicService topicService, AnalyzeForumService forumService, ObjectContainer db) {
        this.topicService = topicService;
        this.forumService = forumService;
        this.db = db;
    }

    public void digCodes(final Game game) {
        forumService.findTopics(game);
        db.store(game);
        db.commit();

        ExecutorService executor = new ThreadPoolExecutor(5, 15, 120, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(100));
        for (final Topic topic : game.getTopics().values()) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    topicService.searchForCodes(topic, new MyForumAnalyzeCallback(game));
                    db.store(topic);
                    db.commit();
                }
            });
        }

        try {
            executor.shutdown();
            if (!executor.awaitTermination(15, TimeUnit.MINUTES)) {
                LOGGER.warn("Too long operation");
            }
        } catch (InterruptedException e) {
            LOGGER.error("Error", e);
        }
//        //Store updated topics list
        db.store(game);
        db.commit();
    }

    private class MyForumAnalyzeCallback implements AnalyzeTopicService.ForumAnalyzeCallback {
        private Game game;

        public MyForumAnalyzeCallback(Game game) {
            this.game = game;
        }

        public void codeFound(String code) {
            ClanInvite clanInvite = new ClanInvite(code, game);
            if (db.queryByExample(clanInvite).size() == 0) {
                clanInvite.setStatus(ClanInviteStatus.DIGGED);
                db.store(clanInvite);
            }
        }
    }
}
package net.ushkinaz.storm8.digger.forum;

import com.db4o.ObjectContainer;
import com.google.inject.Inject;
import net.ushkinaz.storm8.digger.LiveCodesAnalyzerService;
import net.ushkinaz.storm8.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ForumCodesDigger implements CodesDigger {
    private static final Logger LOGGER = LoggerFactory.getLogger(ForumCodesDigger.class);

    private TopicAnalyzerService topicAnalyzerService;
    private ForumAnalyzerService forumAnalyzerService;
    private LiveCodesAnalyzerService liveCodesAnalyzerService;
    private ObjectContainer db;

    @Inject
    public ForumCodesDigger(TopicAnalyzerService topicAnalyzerService, ForumAnalyzerService forumAnalyzerService, LiveCodesAnalyzerService liveCodesAnalyzerService, ObjectContainer db) {
        this.topicAnalyzerService = topicAnalyzerService;
        this.forumAnalyzerService = forumAnalyzerService;
        this.liveCodesAnalyzerService = liveCodesAnalyzerService;
        this.db = db;
    }

    public void digCodes(final Game game) {
        liveCodesAnalyzerService.dig(new MyCodesDiggerCallback(game, ClanInviteSource.LIVE_CODES));

        forumAnalyzerService.findTopics(game);
        db.store(game);
        db.commit();

        ExecutorService executor = new ThreadPoolExecutor(5, 15, 120, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        for (final Topic topic : game.getTopics().values()) {
            if (!topic.arePostsAdded()) {
                LOGGER.debug("No new posts: " + topic);
                continue;
            }

            executor.execute(new Runnable() {
                @Override
                public void run() {
                    topicAnalyzerService.searchForCodes(topic, new MyCodesDiggerCallback(game, ClanInviteSource.FORUM));
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

    private class MyCodesDiggerCallback implements PageDigger.CodesDiggerCallback {
        private Game game;
        private ClanInviteSource inviteSource;

        public MyCodesDiggerCallback(Game game, ClanInviteSource inviteSource) {
            this.game = game;
        }

        public void codeFound(String code) {
            ClanInvite clanInvite = new ClanInvite(code, game);
            if (db.queryByExample(clanInvite).size() == 0) {
                clanInvite.setStatus(ClanInviteStatus.DIGGED);
                clanInvite.setInviteSource(inviteSource);
                db.store(clanInvite);
                db.commit();
            }
        }
    }
}
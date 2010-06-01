package net.ushkinaz.storm8.digger.forum;

import com.db4o.ObjectContainer;
import com.google.inject.Inject;
import net.ushkinaz.storm8.digger.CodesDigger;
import net.ushkinaz.storm8.digger.DBStoringCallback;
import net.ushkinaz.storm8.domain.ClanInviteSource;
import net.ushkinaz.storm8.domain.Game;
import net.ushkinaz.storm8.domain.Topic;
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
    private ObjectContainer db;

    @Inject
    private ForumCodesDigger(TopicAnalyzerService topicAnalyzerService, ForumAnalyzerService forumAnalyzerService, ObjectContainer db) {
        this.topicAnalyzerService = topicAnalyzerService;
        this.forumAnalyzerService = forumAnalyzerService;
        this.db = db;
    }

    public void digCodes(final Game game) {
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
                    topicAnalyzerService.searchForCodes(topic, new DBStoringCallback(game, ClanInviteSource.FORUM, db));
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
}
package net.ushkinaz.storm8.forum;

import com.db4o.ObjectContainer;
import com.google.inject.Inject;
import net.ushkinaz.storm8.domain.ClanInvite;
import net.ushkinaz.storm8.domain.Game;
import net.ushkinaz.storm8.domain.Topic;

import java.util.Collection;

public class ForumCodesDigger implements CodesDigger {
    private AnalyzeTopicService topicService;
    private AnalyzeForumService forumService;
    private ObjectContainer db;

    @Inject
    public ForumCodesDigger(AnalyzeTopicService topicService, AnalyzeForumService forumService, ObjectContainer db) {
        this.topicService = topicService;
        this.forumService = forumService;
        this.db = db;
    }

    public void digCodes(Game game) {
        forumService.findTopics(game);

        db.store(game);
        db.commit();
//        for (Topic topic : game.getTopics()) {
//            topicService.analyze(topic, new MyForumAnalyzeCallback(game));
//        }
    }

    private class MyForumAnalyzeCallback implements AnalyzeTopicService.ForumAnalyzeCallback {
        private Game game;

        public MyForumAnalyzeCallback(Game game) {
            this.game = game;
        }

        public void codesFound(Collection<String> codes) {
            for (String code : codes) {
                ClanInvite clanInvite = new ClanInvite(code, game);
                db.store(clanInvite);
//                ForumCodesDigger.this.clanDao.insertNewClanInvite(clanInvite);
            }
        }
    }
}
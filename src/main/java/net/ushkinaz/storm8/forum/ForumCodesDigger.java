package net.ushkinaz.storm8.forum;

import com.db4o.ObjectContainer;
import com.google.inject.Inject;
import net.ushkinaz.storm8.domain.ClanInvite;
import net.ushkinaz.storm8.domain.Game;
import net.ushkinaz.storm8.domain.Topic;

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

        for (Topic topic : game.getTopics().values()) {
            topicService.searchForCodes(topic, new MyForumAnalyzeCallback(game));
        }

        //Store updated topics list
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
                db.store(clanInvite);
            }
        }
    }
}
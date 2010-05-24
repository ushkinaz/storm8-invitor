package net.ushkinaz.storm8.forum;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.inject.Inject;
import net.ushkinaz.storm8.CodesReader;
import net.ushkinaz.storm8.dao.ClanDao;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ForumCodesDigger implements CodesDigger {
    private AnalyzeForumThreadService forumThreadService;
    private Collection<Integer> topics;
    private ClanDao clanDao;

    @Inject
    public ForumCodesDigger(AnalyzeForumThreadService forumThreadService, CodesReader codesReader, ClanDao clanDao) {
        this.forumThreadService = forumThreadService;
        this.clanDao = clanDao;
        Set<String> topicStrings = new HashSet<String>();

        codesReader.readFromFile("topics.list", topicStrings);

        topics = Collections2.transform(topicStrings, new Function<String, Integer>() {
            public Integer apply(String from) {
                return Integer.parseInt(from);
            }
        });

    }


    public void digCodes(String gameCode) {
        for (Integer topic : topics) {
            forumThreadService.analyze(topic, new MyForumAnalyzeCallback(gameCode));
        }
    }

    private class MyForumAnalyzeCallback implements AnalyzeForumThreadService.ForumAnalyzeCallback {
        private String gameCode;

        public MyForumAnalyzeCallback(String gameCode) {
            this.gameCode = gameCode;
        }

        public void codesFound(Collection<String> codes) {
            for (String code : codes) {
                ForumCodesDigger.this.clanDao.insertNewClan(code, gameCode);
            }
        }
    }
}
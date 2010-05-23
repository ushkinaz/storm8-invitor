package net.ushkinaz.storm8.forum;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.inject.Inject;
import net.ushkinaz.storm8.CodesReader;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class ForumCodesDigger implements CodesDigger {
    private AnalyzeForumThreadService forumThreadService;
    private Collection<Integer> topics;

    @Inject
    public ForumCodesDigger(AnalyzeForumThreadService forumThreadService, CodesReader codesReader) {
        this.forumThreadService = forumThreadService;
        Set<String> topicStrings = new HashSet<String>();

        codesReader.readFromFile("topics.list", topicStrings);

        topics = Collections2.transform(topicStrings, new Function<String, Integer>() {
            public Integer apply(String from) {
                return Integer.parseInt(from);
            }
        });

    }


    public Set<String> digCodes() {
        Set<String> allCodes = new TreeSet<String>();

        for (Integer topic : topics) {
            forumThreadService.analyze(topic, new MyForumAnalyzeCallback(allCodes));
        }
        return allCodes;
    }

    private static class MyForumAnalyzeCallback implements AnalyzeForumThreadService.ForumAnalyzeCallback {
        private final Set<String> allCodes;

        public MyForumAnalyzeCallback(Set<String> allCodes) {
            this.allCodes = allCodes;
        }

        public void codesFound(Collection<String> codes) {
            allCodes.addAll(codes);
        }
    }
}
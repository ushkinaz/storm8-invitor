package net.ushkinaz.storm8.forum;

import com.google.inject.Inject;
import net.ushkinaz.storm8.domain.Game;
import net.ushkinaz.storm8.domain.Topic;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Date: 23.05.2010
 * Created by Dmitry Sidorenko.
 */
public class AnalyzeForumService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AnalyzeForumService.class);

    private final static String FORUM_URL = "http://forums.storm8.com/forumdisplay.php?f={0,number,######}";

    private static final Pattern topicPattern = Pattern.compile("t=(\\d*)\\&amp;page=(\\d*)\"\\>Last Page");
    //private static final Pattern topicPattern = Pattern.compile("<a href=\"showthread.php?s=\\w*&amp;t=(\\d*)&amp;page=(\\d*)\">Last Page</a>");

    private HttpClient httpClient;

    @Inject
    public AnalyzeForumService() {
    }

    public void findTopics(Game game) {
        initHttpClient();
        LOGGER.info("Forum: " + game.getForumId());
        parseTopics(game);
    }

    private void initHttpClient() {
        httpClient = new HttpClient();
    }

    private void parseTopics(Game game) {
        GetMethod pagesMethod = new GetMethod(MessageFormat.format(FORUM_URL, game.getForumId()));
        try {
            int statusCode = httpClient.executeMethod(pagesMethod);
            if (statusCode != 200) {
                throw new IOException("Can't access topics page");
            }
            String page = pagesMethod.getResponseBodyAsString();
            Matcher matcher = topicPattern.matcher(page);
            while (matcher.find()) {
                int topicId = Integer.parseInt(matcher.group(1));
                int lastPage = Integer.parseInt(matcher.group(2));
                LOGGER.info("Found topic: " + topicId);

                Topic topic = new Topic(topicId);
                int index = game.getTopics().indexOf(topic);
                if (index >= 0) {
                    topic = game.getTopics().get(index);
                }

                topic.setPages(lastPage);
                game.getTopics().add(topic);
            }
        } catch (IOException e) {
            LOGGER.error("Error", e);
        }
    }

    public interface ForumAnalyzeCallback {
        void codesFound(Collection<String> codes);
    }
}
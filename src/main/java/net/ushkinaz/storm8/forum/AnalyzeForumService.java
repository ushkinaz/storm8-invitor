package net.ushkinaz.storm8.forum;

import com.google.inject.Inject;
import net.ushkinaz.storm8.domain.Game;
import net.ushkinaz.storm8.domain.Topic;
import net.ushkinaz.storm8.http.HttpClientProvider;
import net.ushkinaz.storm8.http.HttpService;
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
public class AnalyzeForumService extends HttpService{
    private static final Logger LOGGER = LoggerFactory.getLogger(AnalyzeForumService.class);

    private final static String FORUM_URL = "http://forums.storm8.com/forumdisplay.php?f={0,number,######}";

    private static final Pattern topicPattern = Pattern.compile("t=(\\d*)\\&amp;page=(\\d*)\"\\>Last Page");

    @Inject
    public AnalyzeForumService(HttpClientProvider clientProvider) {
        super(clientProvider);
    }

    public void findTopics(Game game) {
        LOGGER.info("Forum: " + game.getForumId());
        parseTopics(game);
    }

    private void parseTopics(Game game) {
        GetMethod pagesMethod = new GetMethod(MessageFormat.format(FORUM_URL, game.getForumId()));
        try {
            int statusCode = getClient().executeMethod(pagesMethod);
            if (statusCode != 200) {
                throw new IOException("Can't access topics page");
            }
            String page = pagesMethod.getResponseBodyAsString();
            Matcher matcher = topicPattern.matcher(page);
            while (matcher.find()) {
                int topicId = Integer.parseInt(matcher.group(1));
                int lastPage = Integer.parseInt(matcher.group(2));
                LOGGER.info("Found topic: " + topicId);

                Topic topic;

                if (game.getTopics().containsKey(topicId)) {
                    topic = game.getTopics().get(topicId);
                }else{
                    topic = new Topic(topicId);
                }

                topic.setPages(lastPage);
                game.getTopics().put(topicId, topic);
            }
        } catch (IOException e) {
            LOGGER.error("Error", e);
        }
    }

    public interface ForumAnalyzeCallback {
        void codesFound(Collection<String> codes);
    }
}
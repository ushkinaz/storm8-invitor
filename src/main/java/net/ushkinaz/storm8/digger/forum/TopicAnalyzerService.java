package net.ushkinaz.storm8.digger.forum;

import com.google.inject.Inject;
import net.ushkinaz.storm8.CodesReader;
import net.ushkinaz.storm8.digger.PageDigger;
import net.ushkinaz.storm8.domain.Topic;
import net.ushkinaz.storm8.http.HttpClientProvider;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Date: 23.05.2010
 * Created by Dmitry Sidorenko.
 */
public class TopicAnalyzerService extends PageDigger {
// ------------------------------ FIELDS ------------------------------

    private static final Logger LOGGER = LoggerFactory.getLogger(TopicAnalyzerService.class);

    private final static String FORUM_TOPIC_URL = "http://forums.storm8.com/showthread.php?t={0,number,######}";
    private final static String FORUM_TOPIC_PAGE_URL = "http://forums.storm8.com/showthread.php?t={0,number,######}&page={1,number,######}";


    private static final Pattern pagePattern = Pattern.compile(".*page=(\\d*)\" title=\"Last Page.*", Pattern.DOTALL);

    private static final Pattern postPattern = Pattern.compile("<!-- message -->(.*?)<!-- / message -->", Pattern.DOTALL);

// --------------------------- CONSTRUCTORS ---------------------------

    @Inject
    private TopicAnalyzerService(CodesReader codesReader, HttpClientProvider clientProvider) {
        super(codesReader, clientProvider);
    }

// -------------------------- OTHER METHODS --------------------------

    public void searchForCodes(Topic topic, CodesDiggerCallback callback) {
        try {
            LOGGER.info("Searching topic: " + topic);
            int count = getPagesCount(topic.getTopicId());
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Topic = " + topic.getTopicId() + " Pages: " + count);
            }

            walkThroughPages(topic, count, callback);
        } catch (IOException e) {
            LOGGER.error("Error", e);
        }
    }

    private int getPagesCount(int topicId) throws IOException {
        int count = 0;
        GetMethod pagesMethod = new GetMethod(MessageFormat.format(FORUM_TOPIC_URL, topicId));
        int statusCode = getClient().executeMethod(pagesMethod);
        if (statusCode != 200) {
            throw new IOException("Can't access topic page");
        }
        String page = pagesMethod.getResponseBodyAsString();
        Matcher matcher = pagePattern.matcher(page);
        if (matcher.matches()) {
            count = Integer.parseInt(matcher.group(1));
        }
        return count;
    }

    private void walkThroughPages(Topic topic, int count, CodesDiggerCallback callback) {
        //Page 0 and page 1 are the same. Ignore the fact.
        for (int page = topic.getLastProcessedPage(); page <= count; page++) {
            LOGGER.info("Topic = " + topic.getTopicId() + ", page = " + page);
            try {
                GetMethod pageMethod = new GetMethod(MessageFormat.format(FORUM_TOPIC_PAGE_URL, topic.getTopicId(), page));
                int statusCode = getClient().executeMethod(pageMethod);
                if (statusCode != 200) {
                    throw new IOException("Can't access topic page");
                }
                String pageBuffer = pageMethod.getResponseBodyAsString();
                Matcher matcher = postPattern.matcher(pageBuffer);
                while (matcher.find()) {
                    String post = matcher.group(1);
                    parsePost(post, callback);
                }
                topic.setLastProcessedPage(page);
            } catch (IOException e) {
                LOGGER.error("Error", e);
            }
        }
    }
}

package net.ushkinaz.storm8.digger.forum;

import com.google.inject.Inject;
import net.ushkinaz.storm8.digger.PageDigger;
import net.ushkinaz.storm8.domain.Topic;
import net.ushkinaz.storm8.http.HttpHelper;
import net.ushkinaz.storm8.http.HttpService;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Date: 23.05.2010
 * Created by Dmitry Sidorenko.
 */
public class TopicAnalyzerService extends HttpService {
// ------------------------------ FIELDS ------------------------------

    private static final Logger LOGGER = LoggerFactory.getLogger(TopicAnalyzerService.class);

    private static final String FORUM_TOPIC_URL = "http://forums.storm8.com/showthread.php?t={0,number,######}";
    private static final String FORUM_TOPIC_PAGE_URL = "http://forums.storm8.com/showthread.php?t={0,number,######}&page={1,number,######}";
    private static final Pattern pagePattern = Pattern.compile(".*page=(\\d*)\" title=\"Last Page.*", Pattern.DOTALL);
    private static final Pattern postPattern = Pattern.compile("<!-- message -->(.*?)<!-- / message -->", Pattern.DOTALL);

    private PageDigger pageDigger;

// --------------------------- CONSTRUCTORS ---------------------------

    public TopicAnalyzerService() {
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    @Inject
    public void setPageDigger(PageDigger pageDigger) {
        this.pageDigger = pageDigger;
    }

// -------------------------- OTHER METHODS --------------------------

    public void searchForCodes(Topic topic, PageDigger.CodesDiggerCallback callback) {
        LOGGER.info("Searching topic: " + topic);
        int count = getPagesCount(topic.getTopicId());
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Topic = " + topic.getTopicId() + " Pages: " + count);
        }

        walkThroughPages(topic, count, callback);
    }

    private int getPagesCount(int topicId){
        int count = 0;
        GetMethod pagesMethod = new GetMethod(MessageFormat.format(FORUM_TOPIC_URL, topicId));
        Matcher matcher = HttpHelper.getHttpMatcher(getClient(), pagesMethod, pagePattern);
        if (matcher.matches()) {
            count = Integer.parseInt(matcher.group(1));
        }
        return count;
    }

    private void walkThroughPages(Topic topic, int count, PageDigger.CodesDiggerCallback callback) {
        //Page 0 and page 1 are the same. Ignore the fact.
        for (int page = topic.getLastProcessedPage(); page <= count; page++) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Topic = " + topic.getTopicId() + ", page = " + page);
            }
            GetMethod pageMethod = new GetMethod(MessageFormat.format(FORUM_TOPIC_PAGE_URL, topic.getTopicId(), page));
            Matcher matcher = HttpHelper.getHttpMatcher(getClient(), pageMethod, postPattern);
            while (matcher.find()) {
                String post = matcher.group(1);
                pageDigger.parsePost(post, callback);
            }
            topic.setLastProcessedPage(page);
        }
    }
}

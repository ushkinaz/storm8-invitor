package net.ushkinaz.storm8.forum;

import com.google.inject.Inject;
import net.ushkinaz.storm8.CodesReader;
import net.ushkinaz.storm8.domain.Topic;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Date: 23.05.2010
 * Created by Dmitry Sidorenko.
 */
public class AnalyzeTopicService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AnalyzeTopicService.class);

    private final static String FORUM_TOPIC_URL = "http://forums.storm8.com/showthread.php?t={0,number,######}";
    private final static String FORUM_TOPIC_PAGE_URL = "http://forums.storm8.com/showthread.php?t={0,number,######}&page={1,number,######}";


    private static final Pattern pagePattern = Pattern.compile(".*page=(\\d*)\" title=\"Last Page.*", Pattern.DOTALL);

    private static final Pattern postPattern = Pattern.compile("<!-- message -->(.*?)<!-- / message -->", Pattern.DOTALL);

    private static final String CODE_PATTERN = "\\w{5}";
    private static final Pattern codePattern = Pattern.compile("\\W(" + CODE_PATTERN + ")\\W");

    private HashSet<String> blackList;
    private ThreadLocal<HttpClient> httpClientThreadLocal = new ThreadLocal<HttpClient>();

    @Inject
    public AnalyzeTopicService(CodesReader codesReader) {
        blackList = new HashSet<String>();
        codesReader.readFromFile("black.list", blackList);
    }

    public void searchForCodes(Topic topic, ForumAnalyzeCallback callback) {
        try {
            if (httpClientThreadLocal.get() == null) {
                httpClientThreadLocal.set(initHttpClient());
            }
            LOGGER.info("Topic: " + topic);
            int count = getPagesCount(topic.getTopicId());
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Topic = " + topic.getTopicId() + " Pages: " + count);
            }

            walkThroughPages(topic, count, callback);

        } catch (IOException e) {
            LOGGER.error("Error", e);
        }
    }

    private void walkThroughPages(Topic topic, int count, ForumAnalyzeCallback callback) {
        //Page 0 and page 1 are the same. Ignore the fact.
        for (int page = topic.getLastProcessedPage(); page <= count; page++) {
            LOGGER.info("Topic = " + topic.getTopicId() + ", page = " + page);
            try {
                GetMethod pageMethod = new GetMethod(MessageFormat.format(FORUM_TOPIC_PAGE_URL, topic.getTopicId(), page));
                int statusCode = httpClientThreadLocal.get().executeMethod(pageMethod);
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

    private void parsePost(String post, ForumAnalyzeCallback callback) {
        Matcher matcher = codePattern.matcher(post);
        while (matcher.find()) {
            String code = matcher.group(1).toUpperCase();
            if (blackList.contains(code)) {
                continue;
            }
            LOGGER.info("Found code: " + code);
            callback.codeFound(code);
        }
    }

    private HttpClient initHttpClient() {
        HttpClient httpClient = new HttpClient();
        httpClient.getParams().setCookiePolicy(CookiePolicy.RFC_2109);
//        httpClient.getParams().setParameter(HttpMethodParams.USER_AGENT, "Mozilla/5.0 (Linux; U; Android 2.1; ru-ru; HTC Legend Build/ERD79) AppleWebKit/530.17 (KHTML, like Gecko) Version/4.0 Mobile Safari/530.17");
        return httpClient;
    }

    private int getPagesCount(int topicId) throws IOException {
        int count = 0;
        GetMethod pagesMethod = new GetMethod(MessageFormat.format(FORUM_TOPIC_URL, topicId));
        int statusCode = httpClientThreadLocal.get().executeMethod(pagesMethod);
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

    public interface ForumAnalyzeCallback {
        void codeFound(String code);
    }
}

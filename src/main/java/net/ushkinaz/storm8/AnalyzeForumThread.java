package net.ushkinaz.storm8;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

/**
 * Date: 23.05.2010
 * Created by Dmitry Sidorenko.
 */
public class AnalyzeForumThread {
    private static final Logger LOGGER = LoggerFactory.getLogger(AnalyzeForumThread.class);

    private final static String FORUM_THREAD_URL = "http://forums.storm8.com/showthread.php?t={0}";
    private final static String FORUM_THREAD_PAGE_URL = "http://forums.storm8.com/showthread.php?t={0}&page={1}";

    private int topicId;

    private List<String> codes;

    private ForumAnalyzeCallback callback;
    private HttpClient httpClient;

    public AnalyzeForumThread(int topicId, ForumAnalyzeCallback callback) {
        this.topicId = topicId;
        this.callback = callback;
    }

    public void analyze() {
        try {
            initHttpClient();

            getPagesCount(httpClient);
            walkThroughPages();

            callback.codesFound(codes);
        } catch (IOException e) {
            LOGGER.error("Error", e);
        }
    }

    private void walkThroughPages() {

    }

    private void initHttpClient() {
        httpClient = new HttpClient();
        httpClient.getParams().setCookiePolicy(CookiePolicy.RFC_2109);
        httpClient.getParams().setParameter(HttpMethodParams.USER_AGENT, "Mozilla/5.0 (Linux; U; Android 2.1; ru-ru; HTC Legend Build/ERD79) AppleWebKit/530.17 (KHTML, like Gecko) Version/4.0 Mobile Safari/530.17");
    }

    private void getPagesCount(HttpClient httpClient) throws IOException {
        GetMethod pagesMethod = new GetMethod(MessageFormat.format(FORUM_THREAD_URL, topicId));
        int statusCode = httpClient.executeMethod(pagesMethod);
        if (statusCode != 200) {
            return;
        }
        //page=54" title="Last Page
        String page = pagesMethod.getResponseBodyAsString();
    }

    private interface ForumAnalyzeCallback {
        void codesFound(List<String> codes);
    }
}

package net.ushkinaz.storm8.forum;

import com.google.inject.Inject;
import net.ushkinaz.storm8.CodesReader;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Date: 23.05.2010
 * Created by Dmitry Sidorenko.
 */
//TODO: store where parsing stopped last time, to avoid reparsing
public class AnalyzeForumThreadService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AnalyzeForumThreadService.class);

    private final static String FORUM_THREAD_URL = "http://forums.storm8.com/showthread.php?t={0,number,######}";
    private final static String FORUM_THREAD_PAGE_URL = "http://forums.storm8.com/showthread.php?t={0,number,######}&page={1,number,######}";


    private static final Pattern pagePattern = Pattern.compile(".*page=(\\d*)\" title=\"Last Page.*", Pattern.DOTALL);

    private static final Pattern postPattern = Pattern.compile("<!-- message -->(.*?)<!-- / message -->", Pattern.DOTALL);

    private static final String CODE_PATTERN = "\\w{5}";
    private static final Pattern codePattern = Pattern.compile("\\W(" + CODE_PATTERN + ")\\W");


    private HttpClient httpClient;
    private HashSet<String> blackList;

    @Inject
    public AnalyzeForumThreadService(CodesReader codesReader) {
        blackList = new HashSet<String>();
        codesReader.readFromFile("black.list", blackList);
    }

    public void analyze(int topicId, ForumAnalyzeCallback callback) {
        try {
            initHttpClient();
            LOGGER.info("Topic: " + topicId);
            int count = getPagesCount(httpClient, topicId);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Pages: " + count);
            }

            callback.codesFound(walkThroughPages(topicId, count));

        } catch (IOException e) {
            LOGGER.error("Error", e);
        }
    }

    private Collection<String> walkThroughPages(int topicId, int count) {
        Collection<String> codes = new HashSet<String>(1000);
        for (int page = 1; page <= count; page++) {
            LOGGER.info("Page: " + page);
            try {
                GetMethod pageMethod = new GetMethod(MessageFormat.format(FORUM_THREAD_PAGE_URL, topicId, page));
                int statusCode = httpClient.executeMethod(pageMethod);
                if (statusCode != 200) {
                    throw new IOException("Can't access thread page");
                }
                String pageBuffer = pageMethod.getResponseBodyAsString();
                Matcher matcher = postPattern.matcher(pageBuffer);
                while (matcher.find()) {
                    String post = matcher.group(1);
                    parsePost(post, codes);
                }
            } catch (IOException e) {
                LOGGER.error("Error", e);
            }
        }

        return codes;
    }

    private void parsePost(String post, Collection<String> codes) {
        Matcher matcher = codePattern.matcher(post);
        while (matcher.find()) {
            String code = matcher.group(1).toUpperCase();
            if (blackList.contains(code)) {
                continue;
            }
            LOGGER.info("Found code: " + code);
            codes.add(code);
        }
    }

    private void initHttpClient() {
        httpClient = new HttpClient();
//        httpClient.getParams().setCookiePolicy(CookiePolicy.RFC_2109);
//        httpClient.getParams().setParameter(HttpMethodParams.USER_AGENT, "Mozilla/5.0 (Linux; U; Android 2.1; ru-ru; HTC Legend Build/ERD79) AppleWebKit/530.17 (KHTML, like Gecko) Version/4.0 Mobile Safari/530.17");
    }

    private int getPagesCount(HttpClient httpClient, Object topicId) throws IOException {
        int count = 0;
        GetMethod pagesMethod = new GetMethod(MessageFormat.format(FORUM_THREAD_URL, topicId));
        int statusCode = httpClient.executeMethod(pagesMethod);
        if (statusCode != 200) {
            throw new IOException("Can't access thread page");
        }
        String page = pagesMethod.getResponseBodyAsString();
        Matcher matcher = pagePattern.matcher(page);
        if (matcher.matches()) {
            count = Integer.parseInt(matcher.group(1));
        }
        return count;
    }

    public interface ForumAnalyzeCallback {
        void codesFound(Collection<String> codes);
    }
}

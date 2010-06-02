package net.ushkinaz.storm8.http;

import com.google.inject.Inject;
import net.ushkinaz.storm8.domain.Player;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.Map;
import java.util.Random;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Sends requests to a given game.
 * Objects can be reused for different requests per game.
 */
public class GameRequestor extends HttpService {
// ------------------------------ FIELDS ------------------------------

    private static final Logger LOGGER = getLogger(GameRequestor.class);

    static final String USER_AGENT = "Mozilla/5.0 (Linux; U; Android 2.1; ru-ru; HTC Legend Build/ERD79) AppleWebKit/530.17 (KHTML, like Gecko) Version/4.0 Mobile Safari/530.17";
    static final String CONTENT_TYPE = "application/x-www-form-urlencoded";
    static final String ACCEPT_CHARSET = "Accept-Charset: utf-8, iso-8859-1, utf-16, *;q=0.7";
    static final String ACCEPT = "application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5,application/youtube-client";
    private static final int SLEEP_BASE = 1;
    private static final int SLEEP = 2;

    private Random random;
    private Player player;

// --------------------------- CONSTRUCTORS ---------------------------

    public GameRequestor() {
        random = new Random();
    }

    @Inject
    public void setPlayer(Player player) {
        this.player = player;
    }

    // -------------------------- OTHER METHODS --------------------------

    @Override
    protected void initHttpClient(HttpClient httpClient) {
        super.initHttpClient(httpClient);    //To change body of overridden methods use File | Settings | File Templates.
        HttpState initialState = new HttpState();

        for (Map.Entry<String, String> cookieEntry : player.getCookies().entrySet()) {
            Cookie cookie = new Cookie(player.getGame().getDomain(), cookieEntry.getKey(), cookieEntry.getValue(), "/", null, false);
            initialState.addCookie(cookie);
        }

        httpClient.setState(initialState);
        httpClient.getParams().setCookiePolicy(CookiePolicy.RFC_2109);
        httpClient.getParams().setParameter(HttpMethodParams.USER_AGENT, USER_AGENT);
    }

    /**
     * @param requestURL      url
     * @param postBodyFactory factory to create request body
     * @return HTTP response
     */
    public String postRequest(String requestURL, PostBodyFactory postBodyFactory){
        String asString = "";
        try {
            PostMethod postMethod = createPostMethod(requestURL, postBodyFactory);
            getClient().executeMethod(postMethod);
            //randomlySleep();
            asString = postMethod.getResponseBodyAsString();
        } catch (IOException e) {
            LOGGER.error("Error in IO", e);
        }
        return asString;
    }

    /**
     * Creates PostMethod.
     *
     * @param requestURL      url to request
     * @param postBodyFactory create body
     * @return post method
     */
    protected PostMethod createPostMethod(String requestURL, PostBodyFactory postBodyFactory) {
        //TODO: add PostMethod pooling

        PostMethod postMethod = new PostMethod(requestURL);
        postMethod.addRequestHeader("Referer", requestURL);
        postMethod.addRequestHeader("Origin", player.getGame().getGameURL());
        postMethod.addRequestHeader("Accept", ACCEPT);
        postMethod.addRequestHeader("Content-type", CONTENT_TYPE);
        postMethod.addRequestHeader("Accept-Charset", ACCEPT_CHARSET);

        if (postBodyFactory != null) {
            postMethod.setRequestBody(postBodyFactory.createBody());
        }
        return postMethod;
    }

    private void randomlySleep() {
        if (!LOGGER.isDebugEnabled()) {
            try {
                Thread.sleep(random.nextInt(SLEEP_BASE + SLEEP));
            } catch (InterruptedException e) {
                LOGGER.error("Error", e);
            }
        }
    }
}
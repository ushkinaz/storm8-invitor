/*
 * Copyright (c) 2010-2010, Dmitry Sidorenko. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
import java.net.SocketException;
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
    private static final int SLEEP_BASE = 5000;
    private static final int SLEEP = 10000;

    private Random random;
    private Player player;

// --------------------------- CONSTRUCTORS ---------------------------

    @Inject
    public GameRequestor(Player player) {
        this.player = player;
        random = new Random();
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
     * @throws PageExpiredException thrown when timestamp for requestURL is exired
     */
    public String postRequest(String requestURL, PostBodyFactory postBodyFactory) throws PageExpiredException {
        String asString = "";
        try {
            PostMethod postMethod = createPostMethod(requestURL, postBodyFactory);
            getClient().executeMethod(postMethod);
            asString = postMethod.getResponseBodyAsString();
        } catch (SocketException e) {
            LOGGER.error("Error requesting:" + requestURL, e);
            randomlySleep();
        } catch (IOException e) {
            LOGGER.error("Error requesting:" + requestURL, e);
        }
        if (asString.contains("Error: The profile for the requested player cannot be displayed at this time.")) {
            throw new PageExpiredException(requestURL);
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

        postMethod.setRequestBody(postBodyFactory.createBody());
        return postMethod;
    }

    private void randomlySleep() {
        if (!LOGGER.isDebugEnabled()) {
            try {
                Thread.sleep(SLEEP_BASE + random.nextInt(SLEEP));
            } catch (InterruptedException e) {
                LOGGER.error("Error", e);
            }
        }
    }
}
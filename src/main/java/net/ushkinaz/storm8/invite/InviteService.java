package net.ushkinaz.storm8.invite;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.ushkinaz.storm8.dao.ClanDao;
import net.ushkinaz.storm8.domain.Game;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.slf4j.Logger;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Random;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Date: 23.05.2010
 * Created by Dmitry Sidorenko.
 */
@Singleton
public class InviteService {
    private static final Logger LOGGER = getLogger(InviteService.class);

    private static final String USER_AGENT = "Mozilla/5.0 (Linux; U; Android 2.1; ru-ru; HTC Legend Build/ERD79) AppleWebKit/530.17 (KHTML, like Gecko) Version/4.0 Mobile Safari/530.17";
    private static final String CONTENT_TYPE = "application/x-www-form-urlencoded";
    private static final String ACCEPT_CHARSET = "Accept-Charset: utf-8, iso-8859-1, utf-16, *;q=0.7";
    private static final String ACCEPT = "application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5,application/youtube-client";

    private static final String FORM_ACTION = "action";
    private static final String FORM_MOBCODE = "mobcode";
    private static final String HTTP_PROXY_HOST = "http.proxyHost";
    private static final String HTTP_PROXY_PORT = "http.proxyPort";

    private Random random;
    private InviteParser inviteParser;
    private ClanDao clanDao;

    @Inject
    public InviteService(ClanDao clanDao, InviteParser inviteParser) throws Exception {
        this.clanDao = clanDao;
        this.random = new Random();
        this.inviteParser = inviteParser;
    }

    private HttpClient initHttpClient(Game game) {
        HttpState initialState = new HttpState();

        for (Map.Entry<String, String> cookieEntry : game.getCookies().entrySet()) {
            Cookie cookie = new Cookie(game.getDomain(), cookieEntry.getKey(), cookieEntry.getValue(), "/", null, false);
            initialState.addCookie(cookie);
        }


        HttpClient httpClient = new HttpClient();

        if (System.getProperty(HTTP_PROXY_HOST) != null) {
            httpClient.getHostConfiguration().setProxy(System.getProperty(HTTP_PROXY_HOST), Integer.getInteger(HTTP_PROXY_PORT, 3128));
        }

        httpClient.setState(initialState);
        httpClient.getParams().setCookiePolicy(CookiePolicy.RFC_2109);
        httpClient.getParams().setParameter(HttpMethodParams.USER_AGENT, USER_AGENT);
        return httpClient;
    }

    /**
     * Invites clans for given game.
     * All clan codes should be in DB by that time.
     *
     * @param game game to use invitations
     * @throws IOException an exception
     */
    public void inviteClans(Game game) throws IOException {

        HttpClient httpClient = initHttpClient(game);

        goThroughDB(httpClient, game);
    }

    private void goThroughDB(HttpClient httpClient, Game game) throws IOException {
        ResultSet set = clanDao.getByStatus(null, game.getGameCode());
        try {
            while (set.next()) {
                try {
                    String code = set.getString(1);
                    invite(code, game, httpClient);
                } catch (SQLException e) {
                    LOGGER.error("Error", e);
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error", e);
        } finally {
            try {
                set.close();
            } catch (SQLException e) {
                LOGGER.error("Error", e);
            }
        }
    }

    private void invite(String clanCode, Game game, HttpClient httpClient) throws IOException {
        if (clanDao.isInvited(clanCode, game.getGameCode())) {
            LOGGER.debug("Skipping:" + clanCode);
            return;
        }

        clanDao.insertNewClan(clanCode, game.getGameCode());
        PostMethod postMethod = createPostMethod(clanCode, game);

        LOGGER.debug("Inviting: " + clanCode);

        int status;
        status = httpClient.executeMethod(postMethod);
        LOGGER.debug("Res: " + status);

        inviteParser.parseResult(postMethod.getResponseBodyAsString(), clanCode, game.getGameCode());

        randomlySleep();
    }

    private PostMethod createPostMethod(String clanCode, Game game) {
        //TODO: add PostMethod pooling

        PostMethod postMethod = new PostMethod(game.getClansURL());
        postMethod.addRequestHeader("Referer", game.getClansURL());
        postMethod.addRequestHeader("Origin", game.getGameURL());
        postMethod.addRequestHeader("Accept", ACCEPT);
        postMethod.addRequestHeader("Content-type", CONTENT_TYPE);
        postMethod.addRequestHeader("Accept-Charset", ACCEPT_CHARSET);

        NameValuePair[] request = {
                new NameValuePair(FORM_ACTION, "Invite"),
                new NameValuePair(FORM_MOBCODE, clanCode)
        };
        postMethod.setRequestBody(request);
        return postMethod;
    }

    private void randomlySleep() {
        if (!LOGGER.isDebugEnabled()) {
            try {
                Thread.sleep(random.nextInt(100));
            } catch (InterruptedException e) {
                LOGGER.error("Error", e);
            }
        }
    }
}

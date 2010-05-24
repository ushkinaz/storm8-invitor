package net.ushkinaz.storm8.invite;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.ushkinaz.storm8.CodesReader;
import net.ushkinaz.storm8.dao.ClanDao;
import net.ushkinaz.storm8.domain.Game;
import net.ushkinaz.storm8.forum.CodesDigger;
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
import java.util.Collection;
import java.util.HashSet;
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

    private static final String CODES_FILENAME = "codes.list";

    private HttpClient httpClient = initHttpClient();
    private Random random;
    private InviteParser inviteParser;
    private ClanDao clanDao;
    private Collection<String> codes;
    private CodesDigger codesDigger;
    private Game game;
    private static final String CONTENT_TYPE = "application/x-www-form-urlencoded";
    private static final String ACCEPT_CHARSET = "Accept-Charset: utf-8, iso-8859-1, utf-16, *;q=0.7";
    private static final String ACCEPT = "application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5,application/youtube-client";

    @Inject
    public InviteService(ClanDao clanDao, InviteParser inviteParser, CodesReader codesReader, CodesDigger codesDigger, Game game) throws Exception {
        this.clanDao = clanDao;
        this.random = new Random();
        this.inviteParser = inviteParser;
        this.codesDigger = codesDigger;
        this.game = game;

        codes = new HashSet<String>();
        codesReader.readFromFile(CODES_FILENAME, codes);

        initHttpClient();
    }

    private HttpClient initHttpClient() {
        HttpState initialState = new HttpState();

        for (Map.Entry<String, String> cookieEntry : game.getCookies().entrySet()) {
            Cookie cookie = new Cookie(game.getDomain(), cookieEntry.getKey(), cookieEntry.getValue(), "/", null, false);
            initialState.addCookie(cookie);
        }


        HttpClient httpClient = new HttpClient();

        httpClient.setState(initialState);
        httpClient.getParams().setCookiePolicy(CookiePolicy.RFC_2109);
        httpClient.getParams().setParameter(HttpMethodParams.USER_AGENT, "Mozilla/5.0 (Linux; U; Android 2.1; ru-ru; HTC Legend Build/ERD79) AppleWebKit/530.17 (KHTML, like Gecko) Version/4.0 Mobile Safari/530.17");
        return httpClient;
    }

    public void shutdown() {
        clanDao.shutdown();
    }

    public void inviteClans() throws IOException {
        codesDigger.digCodes();

        for (String code : codes) {
            invite(code);
        }

        workOnDB();
    }

    private void workOnDB() throws IOException {
        ResultSet set = clanDao.getNotInvited();
        try {
            while (set.next()) {
                try {
                    String code = set.getString(1);
                    invite(code);
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

    private void invite(String clanCode) throws IOException {
        if (clanDao.isInvited(clanCode)) {
            LOGGER.debug("Skipping:" + clanCode);
            return;
        }

        clanDao.insertNewClan(clanCode);
        PostMethod postMethod = createPostMethod(clanCode);

        LOGGER.debug("Inviting: " + clanCode);

        int status = 0;
//        httpClient.executeMethod(postMethod);
//        inviteParser.parseResult(postMethod.getResponseBodyAsString(), clanCode);

        LOGGER.debug("Res: " + status);
        randomlySleep();
    }

    private PostMethod createPostMethod(String clanCode) {
        PostMethod postMethod = new PostMethod(game.getClansURL());
        postMethod.addRequestHeader("Referer", game.getClansURL());
        postMethod.addRequestHeader("Origin", game.getGameURL());
        postMethod.addRequestHeader("Accept", ACCEPT);
        postMethod.addRequestHeader("Content-type", CONTENT_TYPE);
        postMethod.addRequestHeader("Accept-Charset", ACCEPT_CHARSET);

        NameValuePair[] request = {
                new NameValuePair("action", "Invite"),
                new NameValuePair("mobcode", clanCode)
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

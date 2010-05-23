package net.ushkinaz.storm8.invite;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.ushkinaz.storm8.dao.ClanDao;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Date: 23.05.2010
 * Created by Dmitry Sidorenko.
 */
@Singleton
public class InviteService {
    private static final Logger LOGGER = getLogger(InviteService.class);

    private static final String HTTP_HOST = "http://nl.storm8.com";
//    private static final String HTTP_HOST = "http://ya.ru";
    private static final String CLAN_URI = "/group.php";

    private static final String CODES_FILENAME = "codes.list";

    private HttpClient httpClient = initHttpClient();
    private Random random;
    private InviteParser inviteParser;
    private ClanDao clanDao;

    @Inject
    public InviteService(ClanDao clanDao, InviteParser inviteParser) throws Exception {
        this.clanDao = clanDao;
        this.random = new Random();
        this.inviteParser = inviteParser;
        initHttpClient();
    }

    private HttpClient initHttpClient() {
        HttpState initialState = new HttpState();

        Cookie ascCookie = new Cookie("nl.storm8.com", "asc", "1ec7c54864b2d968f89aa6453b067a9d4bff8a2f;", "/", null, false);
        Cookie stCookie = new Cookie("nl.storm8.com", "st", "2792593%2Cc2e17ffa909fd6d4a6b1bbe219fed884ffddb425%2C1274554582%2C12%2C%2Ca1.54%2C14%2C4%2C10003%2C2010-05-22+11%3A56%3A22%2C%2Cv1_1274554582_5e4680101c1b52f0674b973b4a03b1aaf3043356", "/", null, false);

        initialState.addCookie(ascCookie);
        initialState.addCookie(stCookie);


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
        BufferedReader bufferedReader = new BufferedReader(new FileReader(CODES_FILENAME));
        String newCode;
        try {
            newCode = bufferedReader.readLine();
            do {
                newCode = newCode.trim();
                invite(newCode);
                newCode = bufferedReader.readLine();
            }
            while (newCode != null);
        } finally {
            bufferedReader.close();
        }
    }

    private void invite(String clanCode) throws IOException {
        clanDao.insertNewClan(clanCode);
        PostMethod postMethod = new PostMethod(HTTP_HOST + CLAN_URI);
        postMethod.addRequestHeader("Referer", HTTP_HOST + CLAN_URI);
        postMethod.addRequestHeader("Origin", HTTP_HOST);
        postMethod.addRequestHeader("Accept", "application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5,application/youtube-client");
        postMethod.addRequestHeader("Content-type", "application/x-www-form-urlencoded");
        postMethod.addRequestHeader("Accept-Charset", "Accept-Charset: utf-8, iso-8859-1, utf-16, *;q=0.7");

        NameValuePair[] request = {
                new NameValuePair("action", "Invite"),
                new NameValuePair("mobcode", clanCode)
        };
        postMethod.setRequestBody(request);

        LOGGER.debug("Inviting: " + clanCode);

        int status = 0;
        httpClient.executeMethod(postMethod);
        inviteParser.parseResult(postMethod.getResponseBodyAsString(), clanCode);

        LOGGER.debug("Res: " + status);
        randomlySleep();
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

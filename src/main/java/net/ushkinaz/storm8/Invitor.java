package net.ushkinaz.storm8;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.*;

/**
 * Date: 23.05.2010
 * Created by Dmitry Sidorenko.
 */
public class Invitor {
    private static final Logger LOGGER = getLogger(Invitor.class);

    private static final String HTTP_HOST = "http://nl.storm8.com";
//    private static final String HTTP_HOST = "http://ya.ru";
    private static final String CLAN_URI = "/group.php";

    private static final String CODES_FILENAME = "codes.list";
    private static final String NAME_PATTERN = "([\\w \\S]*)";

    private HttpClient httpClient = initHttpClient();
    private Connection conn;
    private Random random;

    public static void main(String[] args) throws Exception {
        Invitor instance = new Invitor();

        instance.inviteClans();

        instance.shutdown();
    }

    public Invitor() throws Exception {
        try {
            initDB();
        } catch (Exception e) {
            LOGGER.error("Error connecting database", e);
            return;
        }
        initHttpClient();
        random = new Random();
    }

    private HttpClient initHttpClient() {
        HttpState initialState = new HttpState();

        Cookie ascCookie = new Cookie("nl.storm8.com", "asc", "1ec7c54864b2d968f89aa6453b067a9d4bff8a2f;", "/", null, false);
        Cookie stCookie = new Cookie("nl.storm8.com", "st", "2792593%2Cc2e17ffa909fd6d4a6b1bbe219fed884ffddb425%2C1274554582%2C12%2C%2Ca1.54%2C14%2C4%2C10003%2C2010-05-22+11%3A56%3A22%2C%2Cv1_1274554582_5e4680101c1b52f0674b973b4a03b1aaf3043356", "/", null, false);

        //asc=1ec7c54864b2d968f89aa6453b067a9d4bff8a2f;
        //st=2792593%2Cc2e17ffa909fd6d4a6b1bbe219fed884ffddb425%2C1274554582%2C12%2C%2Ca1.54%2C14%2C4%2C10003%2C2010-05-22+11%3A56%3A22%2C%2Cv1_1274554582_5e4680101c1b52f0674b973b4a03b1aaf3043356

        initialState.addCookie(ascCookie);
        initialState.addCookie(stCookie);


        HttpClient httpClient = new HttpClient();

        httpClient.setState(initialState);
        httpClient.getParams().setCookiePolicy(CookiePolicy.RFC_2109);
        httpClient.getParams().setParameter(HttpMethodParams.USER_AGENT, "Mozilla/5.0 (Linux; U; Android 2.1; ru-ru; HTC Legend Build/ERD79) AppleWebKit/530.17 (KHTML, like Gecko) Version/4.0 Mobile Safari/530.17");
        return httpClient;
    }

    private void initDB() throws Exception {
        try {
            try {
                Class.forName("org.hsqldb.jdbcDriver");
            } catch (Exception e) {
                LOGGER.error("ERROR: failed to load HSQLDB JDBC driver.", e);
                throw e;
            }
            conn = DriverManager.getConnection("jdbc:hsqldb:file:c:/temp/inviordb;shutdown=true", "SA", "");
        } catch (SQLException e) {
            LOGGER.error("Error", e);
        }
    }

    private void shutdown() {
        try {
            Statement st = conn.createStatement();
            st.execute("SHUTDOWN");
            conn.close();    // if there are no other open connection
        } catch (SQLException e) {
            LOGGER.error("Error", e);
        }
    }

    private void inviteClans() throws IOException {
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
        insertNewClan(clanCode);
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
        parseResult(postMethod.getResponseBodyAsString(), clanCode);

        LOGGER.debug("Res: " + status);
        randomlySleep();
    }

    private void insertNewClan(String clanCode) {
        try {
            conn.createStatement().executeUpdate("INSERT INTO CLANS (code, date_requested) VALUES ('" + clanCode + "', CURRENT_TIMESTAMP )");
        } catch (SQLException e) {
//            LOGGER.debug("Error", e);
        }
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

    private void parseResult(String response, String clanCode) {
        String clanName;

        Pattern successPattern = Pattern.compile(".*<div class=\"messageBoxSuccess\"><span class=\"success\">Success!</span> You invited " + NAME_PATTERN + " to your clan.</div>.*", Pattern.DOTALL);
        Pattern alreadyInvitedPattern = Pattern.compile(".*<div class=\"messageBoxFail\"><span class=\"fail\">Failure:</span> You already invited " + NAME_PATTERN + " to join your clan.</div>.*", Pattern.DOTALL);
        Pattern inClanPattern = Pattern.compile(".*<div class=\"messageBoxFail\"><span class=\"fail\">Failure:</span> " + NAME_PATTERN + " is already in your clan.</div>.*", Pattern.DOTALL);
        Pattern notFoundPattern = Pattern.compile(".*<div class=\"messageBoxFail\"><span class=\"fail\">Failure:</span> Unable to find anyone with that clan code.</div>.*", Pattern.DOTALL);
        //
        //Pattern alreadyInvitedPattern = Pattern.compile(".*messageBoxFail.*");

        Matcher matcherSuccess = successPattern.matcher(response);
        Matcher matcherAlreadyInvited = alreadyInvitedPattern.matcher(response);
        Matcher matcherInClan = inClanPattern.matcher(response);
        Matcher matcherNotFound = notFoundPattern.matcher(response);

        if (matcherSuccess.matches()) {
            clanName = matcherSuccess.group(1);
            updateClanDB(clanCode, clanName, ClanInviteStatus.REQUESTED);
            LOGGER.info("Requested: " + clanName);
        } else if (matcherAlreadyInvited.matches()) {
            clanName = matcherAlreadyInvited.group(1);
            updateClanDB(clanCode, clanName, ClanInviteStatus.PENDING);
            LOGGER.info("Pending: " + clanName);
        } else if (matcherInClan.matches()) {
            clanName = matcherInClan.group(1);
            updateClanDB(clanCode, clanName, ClanInviteStatus.ACCEPTED);
            LOGGER.info("InClan: " + clanName);
        } else if (matcherNotFound.matches()) {
            //clanName = matcherNotFound.group(1);
            updateClanDB(clanCode, null, ClanInviteStatus.NOT_FOUND);
            LOGGER.info("Unable to find clan with code: " + clanCode);
        } else {
            LOGGER.info("Unknown error");
            logUnknownError(response, clanCode);
        }
    }

    private void updateClanDB(String clanCode, String clanName, ClanInviteStatus status) {
        String updateString;
        updateString = "SET Status=" + status.getStatus() + ", date_updated = CURRENT_TIMESTAMP";
        if (clanName != null) {
            updateString += ", name='" + clanName + "'";
        }
        try {
            String updateQuery = "UPDATE Clans " + updateString + " WHERE code = '" + clanCode + "'";
            conn.createStatement().executeUpdate(updateQuery);
        } catch (SQLException e) {
            LOGGER.error("Error", e);
        }
    }

    private void logUnknownError(String response, String clanCode) {
        PrintWriter writer = null;
        try {
            String fileName = clanCode + ".resp";
            writer = new PrintWriter(new FileWriter(fileName));
            writer.write(response);
            LOGGER.info("Response is written to " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            writer.close();
        }
    }

    enum ClanInviteStatus {
        REQUESTED(1),
        PENDING(2),
        ACCEPTED(4),
        NOT_FOUND(8);

        private int status;

        ClanInviteStatus(int status) {
            this.status = status;
        }

        public int getStatus() {
            return status;
        }
    }
}

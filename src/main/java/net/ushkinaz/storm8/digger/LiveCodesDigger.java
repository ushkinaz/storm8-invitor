package net.ushkinaz.storm8.digger;

import com.db4o.ObjectContainer;
import com.google.inject.Inject;
import net.ushkinaz.storm8.CodesReader;
import net.ushkinaz.storm8.domain.ClanInviteSource;
import net.ushkinaz.storm8.domain.Game;
import net.ushkinaz.storm8.http.HttpClientProvider;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Date: 23.05.2010
 * Created by Dmitry Sidorenko.
 */
public class LiveCodesDigger extends PageDigger implements CodesDigger {
// ------------------------------ FIELDS ------------------------------

    private static final Logger LOGGER = LoggerFactory.getLogger(LiveCodesDigger.class);

    private final static String SITE_URL = "http://getninjaslivecodes.com/";

    private static final Pattern codesPattern = Pattern.compile("User Codes(.*)\\<\\/ul\\>", Pattern.DOTALL);

    private ObjectContainer db;

// --------------------------- CONSTRUCTORS ---------------------------

    @Inject
    private LiveCodesDigger(HttpClientProvider clientProvider, CodesReader codesReader, ObjectContainer db) {
        super(codesReader, clientProvider);
        this.db = db;
        setCodePattern("<li>(\\w{5})</li>");
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface CodesDigger ---------------------

    @Override
    public void digCodes(Game game) {
        LOGGER.info(">> digCodes");
        //Page 0 and page 1 are the same. Ignore the fact.
        try {
            GetMethod pageMethod = new GetMethod(SITE_URL);
            int statusCode = getClient().executeMethod(pageMethod);
            if (statusCode != 200) {
                throw new IOException("Can't access page");
            }
            String pageBuffer = pageMethod.getResponseBodyAsString();
            Matcher matcherCodes = codesPattern.matcher(pageBuffer);
            if (matcherCodes.find()) {
                String strCodes = matcherCodes.group(1);
                parsePost(strCodes, new DBStoringCallback(game, ClanInviteSource.LIVE_CODES, db));
            }
        } catch (IOException e) {
            LOGGER.error("Error", e);
        }
        LOGGER.info("<< digCodes");
    }
}
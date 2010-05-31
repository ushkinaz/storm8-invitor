package net.ushkinaz.storm8.digger;

import com.google.inject.Inject;
import net.ushkinaz.storm8.CodesReader;
import net.ushkinaz.storm8.digger.forum.PageDigger;
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
public class LiveCodesAnalyzerService extends PageDigger {
    private static final Logger LOGGER = LoggerFactory.getLogger(LiveCodesAnalyzerService.class);

    private final static String SITE_URL = "http://getninjaslivecodes.com/";

    //private static final Pattern codesPattern = Pattern.compile("<div class=\"entry\">(.*)end #content", Pattern.DOTALL);
    private static final Pattern codesPattern = Pattern.compile("User Codes(.*)\\<\\/ul\\>", Pattern.DOTALL);
//    private static final Pattern codesPattern = Pattern.compile("User Code([s])", Pattern.DOTALL);

    @Inject
    public LiveCodesAnalyzerService(HttpClientProvider clientProvider, CodesReader codesReader) {
        super(codesReader, clientProvider);
        setCodePattern("<li>(\\w{5})</li>");
    }



    public void dig(CodesDiggerCallback callback) {
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
                parsePost(strCodes, callback);
            }

        } catch (IOException e) {
            LOGGER.error("Error", e);
        }
    }

}
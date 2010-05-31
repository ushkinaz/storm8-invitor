package net.ushkinaz.storm8.digger;

import com.google.inject.Inject;
import net.ushkinaz.storm8.CodesReader;
import net.ushkinaz.storm8.http.HttpClientProvider;
import net.ushkinaz.storm8.http.HttpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Date: 31.05.2010
 * Created by Dmitry Sidorenko.
 */
public class PageDigger extends HttpService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PageDigger.class);

    private static final String CODE_PATTERN = "\\w{5}";
    private Pattern codePattern = Pattern.compile("\\W(" + CODE_PATTERN + ")\\W");
    protected HashSet<String> blackList;

    @Inject
    public PageDigger(CodesReader codesReader, HttpClientProvider clientProvider) {
        super(clientProvider);
        blackList = new HashSet<String>();
        codesReader.readFromFile("black.list", blackList);
    }

    public Pattern getCodePattern() {
        return codePattern;
    }

    protected void setCodePattern(String patternString) {
        this.codePattern = Pattern.compile(patternString);
    }

    protected void parsePost(String post, CodesDiggerCallback callback) {
        Matcher matcher = codePattern.matcher(post);
        while (matcher.find()) {
            String code = matcher.group(1).toUpperCase();
            if (blackList.contains(code)) {
                continue;
            }
            LOGGER.info("Code: " + code);
            callback.codeFound(code);
        }
    }

    public interface CodesDiggerCallback {
        void codeFound(String code);
    }
}

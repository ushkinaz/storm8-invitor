package net.ushkinaz.storm8.digger;

import com.google.inject.Inject;
import net.ushkinaz.storm8.CodesReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Date: 31.05.2010
 * Created by Dmitry Sidorenko.
 */
public class PageDigger {
// ------------------------------ FIELDS ------------------------------

    private static final Logger LOGGER = LoggerFactory.getLogger(PageDigger.class);

    private static final String CODE_PATTERN = "\\w{5}";
    private Set<String> blackList;
    private Pattern codePattern = Pattern.compile("\\W(" + CODE_PATTERN + ")\\W");

// --------------------------- CONSTRUCTORS ---------------------------

    protected PageDigger() {
        blackList = new HashSet<String>();
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public Pattern getCodePattern() {
        return codePattern;
    }

// -------------------------- OTHER METHODS --------------------------

    public void parsePost(String post, CodesDiggerCallback callback) {
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

    protected void setCodePattern(String patternString) {
        this.codePattern = Pattern.compile(patternString);
    }

    @Inject
    public void setCodesReader(CodesReader codesReader) {
        codesReader.readFromFile("black.list", blackList);
    }

// -------------------------- INNER CLASSES --------------------------

    public interface CodesDiggerCallback {
        void codeFound(String code);
    }
}

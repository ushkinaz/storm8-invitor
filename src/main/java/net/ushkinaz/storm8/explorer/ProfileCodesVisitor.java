package net.ushkinaz.storm8.explorer;

import com.google.inject.Inject;
import net.ushkinaz.storm8.digger.PageDigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.ushkinaz.storm8.digger.MatcherHelper.isMatchFound;
import static net.ushkinaz.storm8.digger.MatcherHelper.match;

/**
 *
 * @author Dmitry Sidorenko
 */
public class ProfileCodesVisitor extends ProfileCommentsVisitor {
// ------------------------------ FIELDS ------------------------------

    @SuppressWarnings({"UnusedDeclaration"})
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileCodesVisitor.class);

    private static final Pattern commentPattern = Pattern.compile("<div style=\"font-weight: bold; width: 250px\">(.*?)</div>", Pattern.DOTALL);

    private PageDigger digger;

// --------------------------- CONSTRUCTORS ---------------------------

    public ProfileCodesVisitor() {
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    @Inject
    public void setDigger(PageDigger digger) {
        this.digger = digger;
    }

// -------------------------- OTHER METHODS --------------------------

    @Override
    protected void handleComments(PageDigger.CodesDiggerCallback callback, String commentsBody) {
        Matcher posts = commentPattern.matcher(commentsBody);
        while (isMatchFound(posts)) {
            digger.parsePost(match(posts), callback);
        }
    }
}

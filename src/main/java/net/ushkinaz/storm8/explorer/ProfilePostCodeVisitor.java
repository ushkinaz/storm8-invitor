package net.ushkinaz.storm8.explorer;

import net.ushkinaz.storm8.digger.PageDigger;
import net.ushkinaz.storm8.http.PostBodyFactory;
import org.apache.commons.httpclient.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.ushkinaz.storm8.digger.MatcherHelper.isMatchFound;
import static net.ushkinaz.storm8.digger.MatcherHelper.match;

/**
 * @author Dmitry Sidorenko
 */
public class ProfilePostCodeVisitor extends ProfileCommentsVisitor {
// ------------------------------ FIELDS ------------------------------

    @SuppressWarnings({"UnusedDeclaration"})
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfilePostCodeVisitor.class);

    private static final Pattern postCommentPattern = Pattern.compile("action=\"/(.*)\">");

// --------------------------- CONSTRUCTORS ---------------------------

    public ProfilePostCodeVisitor() {
    }

// --------------------- GETTER / SETTER METHODS ---------------------

// -------------------------- OTHER METHODS --------------------------

    @Override
    protected void handleComments(PageDigger.CodesDiggerCallback callback, String commentsBody) {
        final String commentText = "Please add " + getPlayer().getCode();

        if (commentsBody.contains(commentText)) {
            return;
        }
        Matcher postForm = postCommentPattern.matcher(commentsBody);
        while (isMatchFound(postForm)) {
            String url = getPlayer().getGame().getGameURL() + match(postForm);
            getGameRequestor().postRequest(url, new PostBodyFactory() {
                @Override
                public NameValuePair[] createBody() {
                    return new NameValuePair[]{
                            new NameValuePair("commentText", commentText),
                            new NameValuePair("action", "Post Comment")
                    };
                }
            });
        }
        LOGGER.debug("Code posted");
    }
}
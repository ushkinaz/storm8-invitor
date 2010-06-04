package net.ushkinaz.storm8.explorer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Dmitry Sidorenko
 * @date Jun 4, 2010
 */
public class BlackListEvaluator {
    @SuppressWarnings({"UnusedDeclaration"})
    private static final Logger LOGGER = LoggerFactory.getLogger(BlackListEvaluator.class);

    /**
     * Can we post this comment here?
     *
     * @param commentsBody comments page
     * @param commentText  comment to post
     * @return {@code true} if we can
     */
    public boolean canPost(String commentsBody, String commentText) {
        commentsBody = commentsBody.toUpperCase();

        if (commentsBody.contains(commentText.toUpperCase())) {
            LOGGER.debug("Already posted");
            return false;
        } else if (commentsBody.contains("DO NOT POST")) {
            return false;
        } else if (commentsBody.contains("DON'T POST")) {
            return false;
        } else if (commentsBody.contains("NO CODES")) {
            return false;
        } else if (commentsBody.contains("NO POST")) {
            return false;
        }

        return true;
    }
}

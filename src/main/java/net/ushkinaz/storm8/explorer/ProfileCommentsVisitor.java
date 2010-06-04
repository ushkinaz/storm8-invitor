package net.ushkinaz.storm8.explorer;

import com.google.inject.Inject;
import net.ushkinaz.storm8.digger.DBStoringCallbackFactory;
import net.ushkinaz.storm8.digger.PageDigger;
import net.ushkinaz.storm8.domain.ClanInviteSource;
import net.ushkinaz.storm8.domain.Player;
import net.ushkinaz.storm8.domain.Victim;
import net.ushkinaz.storm8.http.GameRequestor;
import net.ushkinaz.storm8.http.PageExpiredException;
import net.ushkinaz.storm8.http.PostBodyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.ushkinaz.storm8.digger.MatcherHelper.isMatchFound;
import static net.ushkinaz.storm8.digger.MatcherHelper.match;

/**
 * Does something with comments page on victim's profile.
 *
 * @author Dmitry Sidorenko
 * @date Jun 3, 2010
 */
public abstract class ProfileCommentsVisitor implements ProfileVisitor {
// ------------------------------ FIELDS ------------------------------

    @SuppressWarnings({"UnusedDeclaration"})
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileCommentsVisitor.class);
    private static final Pattern commentsPattern = Pattern.compile("<a href=\"/profile.php\\?(.*?)\">Comments</a>");
    private GameRequestor gameRequestor;
    private Player player;
    private DBStoringCallbackFactory callbackFactory;

// --------------------- GETTER / SETTER METHODS ---------------------

    protected GameRequestor getGameRequestor() {
        return gameRequestor;
    }

    @Inject
    public void setGameRequestor(GameRequestor gameRequestor) {
        this.gameRequestor = gameRequestor;
    }

    protected Player getPlayer() {
        return player;
    }

    @Inject
    public void setPlayer(Player player) {
        this.player = player;
    }

    @Inject
    public void setCallbackFactory(DBStoringCallbackFactory callbackFactory) {
        this.callbackFactory = callbackFactory;
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface ProfileVisitor ---------------------

    @Override
    public void visitProfile(Victim victim, String profileHTML) throws PageExpiredException {
        PageDigger.CodesDiggerCallback callback = this.callbackFactory.get(player.getGame(), ClanInviteSource.INGAME_COMMENT);

        Matcher matcherComments = commentsPattern.matcher(profileHTML);
        if (isMatchFound(matcherComments)) {
            String commentsURL = victim.getGame().getGameURL() + "profile.php?" + match(matcherComments);
            String commentsBody = gameRequestor.postRequest(commentsURL, PostBodyFactory.NULL);
            handleComments(callback, commentsBody);
        }
    }

// -------------------------- OTHER METHODS --------------------------

    protected abstract void handleComments(PageDigger.CodesDiggerCallback callback, String commentsBody);
}

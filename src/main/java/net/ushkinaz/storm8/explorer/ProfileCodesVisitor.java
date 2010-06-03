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
 * @author Dmitry Sidorenko
 */
public class ProfileCodesVisitor implements ProfileVisitor {
// ------------------------------ FIELDS ------------------------------

    @SuppressWarnings({"UnusedDeclaration"})
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileCodesVisitor.class);

    static final Pattern commentsPattern = Pattern.compile("<a href=\"/profile.php\\?(.*?)\">Comments</a>");
    static final Pattern commentPattern = Pattern.compile("<div style=\"font-weight: bold; width: 250px\">(.*?)</div>", Pattern.DOTALL);
    private GameRequestor gameRequestor;
    private PageDigger digger;
    private Player player;
    private DBStoringCallbackFactory callbackFactory;

// --------------------- GETTER / SETTER METHODS ---------------------

    public ProfileCodesVisitor() {
    }

    @Inject
    public void setCallback(DBStoringCallbackFactory callbackFactory) {
        this.callbackFactory = callbackFactory;
    }

    @Inject
    public void setDigger(PageDigger digger) {
        this.digger = digger;
    }

    @Inject
    public void setPlayer(Player player) {
        this.player = player;
    }

    @Inject
    public void setGameRequestor(GameRequestor gameRequestor) {
        this.gameRequestor = gameRequestor;
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface ProfileVisitor ---------------------

    @Override
    public void visitProfile(Victim victim, String profileHTML) throws PageExpiredException {
        LOGGER.debug("Visiting: " + victim.getName());
        PageDigger.CodesDiggerCallback callback = this.callbackFactory.get(player.getGame(), ClanInviteSource.INGAME_COMMENT);

        Matcher matcherComments = commentsPattern.matcher(profileHTML);
        if (isMatchFound(matcherComments)) {
            String commentsURL = victim.getGame().getGameURL() + "profile.php?" + match(matcherComments);
            String commentsBody = gameRequestor.postRequest(commentsURL, PostBodyFactory.NULL);
            Matcher posts = commentPattern.matcher(commentsBody);
            while (isMatchFound(posts)) {
                digger.parsePost(match(posts), callback);
            }
        }
    }
}

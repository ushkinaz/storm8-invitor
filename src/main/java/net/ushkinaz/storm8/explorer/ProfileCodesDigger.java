package net.ushkinaz.storm8.explorer;

import com.db4o.ObjectContainer;
import com.google.inject.Inject;
import net.ushkinaz.storm8.configuration.CodesReader;
import net.ushkinaz.storm8.digger.DBStoringCallback;
import net.ushkinaz.storm8.digger.PageDigger;
import net.ushkinaz.storm8.domain.ClanInviteSource;
import net.ushkinaz.storm8.domain.Player;
import net.ushkinaz.storm8.domain.Victim;
import net.ushkinaz.storm8.http.GameRequestor;
import net.ushkinaz.storm8.http.PageExpiredException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.ushkinaz.storm8.digger.MatcherHelper.isMatchFound;
import static net.ushkinaz.storm8.digger.MatcherHelper.match;

/**
 * @author Dmitry Sidorenko
 */
public class ProfileCodesDigger implements ProfileVisitor {
// ------------------------------ FIELDS ------------------------------

    @SuppressWarnings({"UnusedDeclaration"})
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileCodesDigger.class);

    static final Pattern commentsPattern = Pattern.compile("<a href=\"/profile.php\\?(.*?)\">Comments</a>");
    static final Pattern commentPattern = Pattern.compile("<div style=\"font-weight: bold; width: 250px\">(.*?)</div>", Pattern.DOTALL);
    private GameRequestor gameRequestor;
    private ObjectContainer db;
    private PageDigger digger;
    private Player player;

// --------------------- GETTER / SETTER METHODS ---------------------

    public ProfileCodesDigger() {
        digger = new PageDigger();
        digger.setCodesReader(new CodesReader());
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    @Inject
    public void setDb(ObjectContainer db) {
        this.db = db;
    }

    @Inject
    public void setGameRequestor(GameRequestor gameRequestor) {
        this.gameRequestor = gameRequestor;
    }


// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface ProfileVisitor ---------------------

    @Override
    public void visitProfile(Victim victim, String profileHTML) throws PageExpiredException {
        PageDigger.CodesDiggerCallback callback = new DBStoringCallback(player.getGame(), ClanInviteSource.INGAME_COMMENT, db);

        Matcher matcherComments = commentsPattern.matcher(profileHTML);
        if (isMatchFound(matcherComments)) {
            String commentsURL = victim.getGame().getGameURL() + "profile.php?" + match(matcherComments);
            String commentsBody = gameRequestor.postRequest(commentsURL, null);
            if (commentsBody.contains("Error: The profile for the requested player cannot be displayed at this time.")) {
                throw new PageExpiredException();
            }
            Matcher posts = commentPattern.matcher(commentsBody);
            while (isMatchFound(posts)) {
                digger.parsePost(match(posts), callback);
            }
        }
    }
}

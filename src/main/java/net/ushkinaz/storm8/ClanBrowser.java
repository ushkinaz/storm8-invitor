package net.ushkinaz.storm8;

import com.db4o.ObjectContainer;
import com.google.inject.Inject;
import net.ushkinaz.storm8.digger.DBStoringCallback;
import net.ushkinaz.storm8.digger.PageDigger;
import net.ushkinaz.storm8.domain.ClanInviteSource;
import net.ushkinaz.storm8.domain.Player;
import net.ushkinaz.storm8.domain.Victim;
import net.ushkinaz.storm8.http.GameRequestor;
import net.ushkinaz.storm8.http.GameRequestorProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.ushkinaz.storm8.digger.MatcherHelper.*;

/**
 * @author Dmitry Sidorenko
 * @date Jun 1, 2010
 */
public class ClanBrowser {
// ------------------------------ FIELDS ------------------------------

    @SuppressWarnings({"UnusedDeclaration"})
    private static final Logger LOGGER = LoggerFactory.getLogger(ClanBrowser.class);

    private static final String LIST_URL = "group_member.php?groupMemberRange=";
    private static final Pattern profilePattern = Pattern.compile("<a href=\"/profile\\.php\\?puid=(\\d*)&(.*?)\">(.*?)</a><br/>");

    private GameRequestorProvider gameRequestorProvider;
    private GameRequestor gameRequestor;
    private Player player;
    private String clanURL;
    private VictimExaminator victimExaminator;
    private ObjectContainer db;

// --------------------------- CONSTRUCTORS ---------------------------

    public ClanBrowser() {
    }

// --------------------- GETTER / SETTER METHODS ---------------------


    @Inject
    public void setDb(ObjectContainer db) {
        this.db = db;
    }

    @Inject
    public void setVictimExaminator(VictimExaminator victimExaminator) {
        this.victimExaminator = victimExaminator;
    }

    @Inject
    public void setGameRequestorProvider(GameRequestorProvider gameRequestorProvider) {
        this.gameRequestorProvider = gameRequestorProvider;
    }


    public void setPlayer(Player player) {
        this.player = player;
        clanURL = player.getGame().getGameURL() + LIST_URL;
        gameRequestor = gameRequestorProvider.getRequestor(player);
    }

// -------------------------- OTHER METHODS --------------------------

    public String getGoodTarget() {
        scanClan(0);
        return "";
    }

    static final Pattern commentsPattern = Pattern.compile("<a href=\"/profile.php\\?(.*?)\">Comments</a>");
    static final Pattern commentPattern = Pattern.compile("<div style=\"font-weight: bold; width: 250px\">(.*?)</div>", Pattern.DOTALL);

    private void scanClan(int scanFromIndex) {
        String requestURL = clanURL + scanFromIndex;
        String body = gameRequestor.postRequest(requestURL, null);
        Matcher matcher = profilePattern.matcher(body);
        while (isMatchFound(matcher)) {
            LOGGER.info("Scan index: " + scanFromIndex);
            scanFromIndex++;
            int puid = matchInteger(matcher);
            String timestamp = match(matcher, 2);
            String name = match(matcher, 3);
            String profileURL = String.format("%sprofile.php?puid=%s&%s", player.getGame().getGameURL(), puid, timestamp);

            LOGGER.info(name + " = " + profileURL);
            String profileHTML = gameRequestor.postRequest(profileURL, null);
            if (profileHTML.contains("Error: The profile for the requested player cannot be displayed at this time.")) {
                //Restart scan
                LOGGER.info("Restarting scan, timestamp expired");
                scanClan(scanFromIndex);
                break;
            }

            Victim victim = new Victim(puid, player.getGame());
            List<Victim> victims = db.queryByExample(victim);
            victim.setName(name);
            if (victims.size() > 0) {
                victim = victims.get(0);
            }

            PageDigger digger = new PageDigger();
            digger.setCodesReader(new CodesReader());
            PageDigger.CodesDiggerCallback callback = new DBStoringCallback(player.getGame(), ClanInviteSource.INGAME_COMMENT, db);

            Matcher matcherComments = commentsPattern.matcher(profileHTML);
            if (isMatchFound(matcherComments)) {
                String commentsURL = player.getGame().getGameURL() + "profile.php?" + match(matcherComments);
                String commentsBody = gameRequestor.postRequest(commentsURL, null);
                Matcher posts = commentPattern.matcher(commentsBody);
                while (isMatchFound(posts)) {
                    digger.parsePost(match(posts), callback);
                }
            }

//            victimExaminator.examine(victim, profileHTML);
//            db.store(victim);
//            db.commit();
        }
    }
}

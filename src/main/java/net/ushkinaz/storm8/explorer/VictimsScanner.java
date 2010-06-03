package net.ushkinaz.storm8.explorer;

import com.db4o.ObjectContainer;
import com.google.inject.Inject;
import net.ushkinaz.storm8.domain.Player;
import net.ushkinaz.storm8.domain.Victim;
import net.ushkinaz.storm8.http.GameRequestor;
import net.ushkinaz.storm8.http.PageExpiredException;
import net.ushkinaz.storm8.http.PostBodyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.ushkinaz.storm8.digger.MatcherHelper.isMatchFound;
import static net.ushkinaz.storm8.digger.MatcherHelper.match;
import static net.ushkinaz.storm8.digger.MatcherHelper.matchInteger;

/**
 * @author Dmitry Sidorenko
 * @date Jun 3, 2010
 */
public abstract class VictimsScanner {
// ------------------------------ FIELDS ------------------------------

    @SuppressWarnings({"UnusedDeclaration"})
    private static final Logger LOGGER = LoggerFactory.getLogger(VictimsScanner.class);

    private static final Pattern profilePattern = Pattern.compile("<a href=\"/profile\\.php\\?puid=(\\d*)&(.*?)\">(.*?)</a><br/>");
    private GameRequestor gameRequestor;
    private Player player;
    private ObjectContainer db;

// --------------------------- CONSTRUCTORS ---------------------------

    protected VictimsScanner() {
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    protected Player getPlayer() {
        return player;
    }

    @Inject
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

// -------------------------- OTHER METHODS --------------------------

    public void visitVictims(ProfileVisitor profileVisitor) {
        LOGGER.debug(">> visitVictims");
        scanVictims(profileVisitor);
        LOGGER.debug("<< visitVictims");
    }

    /**
     * Scans single page of clan members.
     * Since links on that page will often expire, a page will be reloaded starting at expired link, effectively moving down the list.
     * So, in fact, this method will scan the whole clan.
     *
     * @param profileVisitor visitor to use
     */
    private void scanVictims(ProfileVisitor profileVisitor) {
        String requestURL = getListURL();
        String body = gameRequestor.postRequest(requestURL, PostBodyFactory.NULL);
        Matcher matcher = profilePattern.matcher(body);
        while (isMatchFound(matcher)) {
            int puid = matchInteger(matcher);
            String timeStamp = match(matcher, 2);
            String name = match(matcher, 3);
            String profileURL = String.format("%sprofile.php?puid=%s&%s", player.getGame().getGameURL(), puid, timeStamp);

            try {
                String profileHTML = gameRequestor.postRequest(profileURL, PostBodyFactory.NULL);

                Victim victim = new Victim(puid, player.getGame());
                List<Victim> victims = db.queryByExample(victim);
                victim.setName(name);
                if (victims.size() > 0) {
                    victim = victims.get(0);
                }
                profileVisitor.visitProfile(victim, profileHTML);
                profileVisited(victim);
            } catch (PageExpiredException e) {
                LOGGER.debug("Restarting scan, time stamp expired");
                scanVictims(profileVisitor);
                break;
            }
        }
    }

    /**
     * Called when we successfully visited a profile
     * @param victim victim we visited
     */
    protected abstract void profileVisited(Victim victim);

    protected abstract String getListURL();
}

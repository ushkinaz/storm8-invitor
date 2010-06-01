package net.ushkinaz.storm8;

import com.google.inject.Inject;
import net.ushkinaz.storm8.domain.Player;
import net.ushkinaz.storm8.http.GameRequestor;
import net.ushkinaz.storm8.http.GameRequestorProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Dmitry Sidorenko
 * @date Jun 1, 2010
 */
public class ClanBrowser {
// ------------------------------ FIELDS ------------------------------

    @SuppressWarnings({"UnusedDeclaration"})
    private static final Logger LOGGER = LoggerFactory.getLogger(ClanBrowser.class);

    private static final String LIST_URL = "group_member.php?groupMemberRange=";
    //http://nl.storm8.com/profile.php?puid=2324979&formNonce=7e0bfa44a92ecdb53fc6edaeecb39e9530b10f2e&h=9185e932f51d6adbd85756b21853213c87041f9c
    private static final Pattern profilePattern = Pattern.compile("<a href=\"/profile\\.php\\?puid=(\\d*)&(.*?)\">(.*?)</a><br/>");
    private static final Pattern itemPattern = Pattern.compile("src=\"http://static\\.storm8\\.com/nl/images/equipment/med/(\\d*)m\\.png\\?v=\\d*\"></div>.*?<div>x(\\d*)</div>", Pattern.DOTALL);

    private GameRequestorProvider gameRequestorProvider;
    private GameRequestor gameRequestor;
    private Player player;
    private String clanURL;

// --------------------------- CONSTRUCTORS ---------------------------

    public ClanBrowser() {
    }

// --------------------- GETTER / SETTER METHODS ---------------------

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

    private void scanClan(int scanFrom) {
        String requestURL = clanURL + scanFrom;
        String body = gameRequestor.postRequest(requestURL, null);
        Matcher matcher = profilePattern.matcher(body);
        while (matcher.find()) {
            scanFrom++;
            String puid = matcher.group(1);
            String timestamp = matcher.group(2);
            String name = matcher.group(3);
            String profileURL = MessageFormat.format("{0}profile.php?puid={1}&{2}", player.getGame().getGameURL(), puid, timestamp);
            LOGGER.info(name + " = " + profileURL);
            try {
                inventory(profileURL);
            } catch (PageExpiredException e) {
                LOGGER.info("Page expired");
                scanClan(scanFrom);
                break;
            }
        }
    }

    private void inventory(String profileURL) throws PageExpiredException {
        String profile = gameRequestor.postRequest(profileURL, null);
        Matcher itemMatcher = itemPattern.matcher(profile);
        if (profile.contains("Error: The profile for the requested player cannot be displayed at this time.")) {
            throw new PageExpiredException();
        }
        while (itemMatcher.find()) {
            String itemId = itemMatcher.group(1);
            String itemQuantity = itemMatcher.group(2);
            LOGGER.info("Item: " + itemId + " quantity:" + itemQuantity);
        }
    }
}

package net.ushkinaz.storm8;

import com.google.inject.Inject;
import net.ushkinaz.storm8.domain.Player;
import net.ushkinaz.storm8.http.GameRequestor;
import net.ushkinaz.storm8.http.GameRequestorProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
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
    private static final Pattern profilePattern = Pattern.compile("<a href=\"/profile\\.php\\?(.*?)\">(.*?)</a><br/>");
    private static final Pattern itemPattern = Pattern.compile("src=\"http://static\\.storm8\\.com/nl/images/equipment/med/(\\d*)m\\.png\\?v=\\d*\"></div>.*?<div>x(\\d*)</div>", Pattern.DOTALL);

    private GameRequestorProvider gameRequestorProvider;

// --------------------------- CONSTRUCTORS ---------------------------

    public ClanBrowser() {
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    @Inject
    public void setGameRequestorProvider(GameRequestorProvider gameRequestorProvider) {
        this.gameRequestorProvider = gameRequestorProvider;
    }

// -------------------------- OTHER METHODS --------------------------

    public String getGoodTarget(Player player) {
        GameRequestor gameRequestor = gameRequestorProvider.getRequestor(player);
        try {
            String clanURL = player.getGame().getGameURL() + LIST_URL;
            int scanFrom = 100;
            String body = gameRequestor.postRequest(clanURL + scanFrom, null);
            Matcher matcher = profilePattern.matcher(body);
            while (matcher.find()) {
                String url = matcher.group(1);
                String name = matcher.group(2);
                String profileURL = player.getGame().getGameURL() + "profile.php?" + url;
                LOGGER.info(name + " = " + profileURL);
                String profile = gameRequestor.postRequest(profileURL, null);
                Matcher itemMatcher = itemPattern.matcher(profile);
                while (itemMatcher.find()) {
                    String itemId = itemMatcher.group(1);
                    String itemQuantity = itemMatcher.group(2);
                    LOGGER.info("Item: " + itemId + " quantity:" + itemQuantity);
                }
            }
        } catch (IOException e) {
            LOGGER.error("Error", e);
        }
        return "";
    }
}

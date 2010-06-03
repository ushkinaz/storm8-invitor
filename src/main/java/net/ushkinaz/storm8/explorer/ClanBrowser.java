package net.ushkinaz.storm8.explorer;

import net.ushkinaz.storm8.domain.Victim;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Dmitry Sidorenko
 */
public class ClanBrowser extends VictimsScanner {
// ------------------------------ FIELDS ------------------------------

    @SuppressWarnings({"UnusedDeclaration"})
    private static final Logger LOGGER = LoggerFactory.getLogger(ClanBrowser.class);

    private static final String LIST_URL = "group_member.php?groupMemberRange=";

    private int scanFromIndex;
    private String clanURLBase;

// --------------------------- CONSTRUCTORS ---------------------------

    public ClanBrowser() {
        scanFromIndex = 2480;
    }

// -------------------------- OTHER METHODS --------------------------

    @Override
    protected String getListURL() {
        LOGGER.info("Scan index: " + scanFromIndex);

        if (clanURLBase == null) {
            clanURLBase = getPlayer().getGame().getGameURL() + LIST_URL;
        }

        return clanURLBase + scanFromIndex;
    }

    @Override
    protected void profileVisited(Victim victim) {
        scanFromIndex++;
    }
}

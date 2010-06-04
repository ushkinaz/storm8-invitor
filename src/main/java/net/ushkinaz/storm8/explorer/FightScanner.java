package net.ushkinaz.storm8.explorer;

import net.ushkinaz.storm8.domain.Victim;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Dmitry Sidorenko
 */
public class FightScanner extends VictimsScanner {
// ------------------------------ FIELDS ------------------------------

    @SuppressWarnings({"UnusedDeclaration"})
    private static final Logger LOGGER = LoggerFactory.getLogger(FightScanner.class);

    private static final String LIST_URL = "fight.php";

    private String clanURLBase;

// --------------------------- CONSTRUCTORS ---------------------------

    public FightScanner() {
    }

// -------------------------- OTHER METHODS --------------------------

    @Override
    protected String getListURL() {
        if (clanURLBase == null) {
            clanURLBase = getPlayer().getGame().getGameURL() + LIST_URL;
        }

        return clanURLBase;
    }

    @Override
    protected void profileVisited(Victim victim) {
    }
}
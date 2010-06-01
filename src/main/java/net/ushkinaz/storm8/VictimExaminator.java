package net.ushkinaz.storm8;

import net.ushkinaz.storm8.domain.Victim;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.ushkinaz.storm8.digger.MatcherHelper.isMatchFound;
import static net.ushkinaz.storm8.digger.MatcherHelper.matchInteger;

public class VictimExaminator {
    @SuppressWarnings({"UnusedDeclaration"})
    private static final Logger LOGGER = LoggerFactory.getLogger(VictimExaminator.class);

    static final Pattern itemPattern = Pattern.compile("src=\"http://static\\.storm8\\.com/nl/images/equipment/med/(\\d*)m\\.png\\?v=\\d*\"></div>.*?<div>x(\\d*)</div>", Pattern.DOTALL);

    public VictimExaminator() {
    }

    public Victim examine(Victim victim, String profile) {
        inventory(victim, profile);
        return victim;
    }

    private void inventory(Victim victim, String profile) {
        Matcher itemMatcher = itemPattern.matcher(profile);
        while (isMatchFound(itemMatcher)) {
            int itemId = matchInteger(itemMatcher);
            int itemQuantity = matchInteger(itemMatcher, 2);
            LOGGER.info("Item: " + itemId + " quantity:" + itemQuantity);
            victim.getInventory().put(itemId, itemQuantity);
        }
    }
}
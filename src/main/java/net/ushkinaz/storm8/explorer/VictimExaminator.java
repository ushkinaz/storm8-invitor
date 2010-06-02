package net.ushkinaz.storm8.explorer;

import net.ushkinaz.storm8.domain.Victim;
import net.ushkinaz.storm8.http.PageExpiredException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.ushkinaz.storm8.digger.MatcherHelper.isMatchFound;
import static net.ushkinaz.storm8.digger.MatcherHelper.matchInteger;

public class VictimExaminator implements ProfileVisitor {
    @SuppressWarnings({"UnusedDeclaration"})
    private static final Logger LOGGER = LoggerFactory.getLogger(VictimExaminator.class);

    static final Pattern itemPattern = Pattern.compile("src=\"http://static\\.storm8\\.com/nl/images/equipment/med/(\\d*)m\\.png\\?v=\\d*\"></div>.*?<div>x(\\d*)</div>", Pattern.DOTALL);

    public VictimExaminator() {
    }

    @Override
    public void visitProfile(Victim victim, String profileHTML) throws PageExpiredException {
        inventory(victim, profileHTML);
    }

    private void inventory(Victim victim, String profile) {
        Matcher itemMatcher = itemPattern.matcher(profile);
        while (isMatchFound(itemMatcher)) {
            int itemId = matchInteger(itemMatcher);
            int itemQuantity = matchInteger(itemMatcher, 2);
            LOGGER.debug("Item: " + itemId + " quantity:" + itemQuantity);
            victim.getInventory().put(itemId, itemQuantity);
        }
    }

}
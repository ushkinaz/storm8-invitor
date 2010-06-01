package net.ushkinaz.storm8.digger;

import com.google.inject.Inject;
import net.ushkinaz.storm8.domain.Equipment;
import net.ushkinaz.storm8.domain.Game;
import net.ushkinaz.storm8.domain.Player;
import net.ushkinaz.storm8.http.GameRequestor;
import net.ushkinaz.storm8.http.HttpClientProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Date: 23.05.2010
 * Created by Dmitry Sidorenko.
 */
public class EquipmentAnalyzerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EquipmentAnalyzerService.class);

    private final static String SITE_URL = "http://nl.storm8.com/ajax/getItemList.php?url=/equipment.php&cat=";

    private static final Pattern equipmentPattern = Pattern.compile("<table class=\"equipmentTable\"(.*?)</table>", Pattern.DOTALL);
    private static final Pattern namePattern = Pattern.compile("<div class=\"equipmentName\">(.*?)</div>");
    private static final Pattern attackPattern = Pattern.compile("<div class=\"equipmentInfoItem\">Attack: (\\d*?)</div>");
    private static final Pattern defencePattern = Pattern.compile("<div class=\"equipmentInfoItem\">Defense: (\\d*?)</div>");
    private static final Pattern upkeepPattern = Pattern.compile("Upkeep: .*?([\\d,]*?)</span>", Pattern.DOTALL);
    private static final Pattern imagePattern = Pattern.compile("http://static.storm8.com/nl/images/equipment/med/(\\d*)m.png\\?v=");

    //
    private HttpClientProvider clientProvider;

    @Inject
    private EquipmentAnalyzerService(HttpClientProvider clientProvider) {
        this.clientProvider = clientProvider;
    }


    public void dig(Player player) {
        LOGGER.info(">> dig");
        Game game = player.getGame();
        try {
            GameRequestor gameRequestor = new GameRequestor(player, clientProvider);

            for (int cat = 1; cat < 3; cat++) {
                String pageBuffer = gameRequestor.postRequest(SITE_URL + cat, null);
                Matcher matcherEquipment = equipmentPattern.matcher(pageBuffer);
                while (matcherEquipment.find()) {
                    String equipmentInfo = matcherEquipment.group(1);
                    String name = match(namePattern.matcher(equipmentInfo));
                    int attack = matchInteger(attackPattern.matcher(equipmentInfo));
                    int defence = matchInteger(defencePattern.matcher(equipmentInfo));
                    int upkeep = matchInteger(upkeepPattern.matcher(equipmentInfo));
                    String id = match(imagePattern.matcher(equipmentInfo));

                    Equipment equipment = new Equipment();
                    equipment.setId(id);
                    equipment.setName(name);
                    equipment.setCategory(cat);
                    equipment.setAttack(attack);
                    equipment.setDefence(defence);
                    equipment.setUpkeep(upkeep);

                    game.getEquipment().add(equipment);

                    LOGGER.debug(name + "=" + attack + ":" + defence + "*" + upkeep);
                }
            }
        } catch (IOException e) {
            LOGGER.error("Error", e);
        }
        LOGGER.info("<< dig");
    }

    private String match(Matcher nameMatcher) {
        String result = null;
        if (nameMatcher.find()) {
            result = nameMatcher.group(1);
        }
        return result;
    }

    private int matchInteger(Matcher nameMatcher) {
        String result = match(nameMatcher);
        if (result == null) {
            result = "0";
        }
        return Integer.parseInt(result.replace(",", ""));
    }

}
/*
 * Copyright (c) 2010-2010, Dmitry Sidorenko. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ushkinaz.storm8.explorer;

import com.db4o.ObjectContainer;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.ushkinaz.storm8.digger.MatcherHelper;
import net.ushkinaz.storm8.domain.Equipment;
import net.ushkinaz.storm8.domain.Game;
import net.ushkinaz.storm8.domain.Player;
import net.ushkinaz.storm8.http.GameRequestor;
import net.ushkinaz.storm8.http.PostBodyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.ushkinaz.storm8.digger.MatcherHelper.match;
import static net.ushkinaz.storm8.digger.MatcherHelper.matchInteger;

/**
 * Date: 23.05.2010
 * Created by Dmitry Sidorenko.
 */
@Singleton
public class EquipmentAnalyzerService {
// ------------------------------ FIELDS ------------------------------

    private static final Logger LOGGER = LoggerFactory.getLogger(EquipmentAnalyzerService.class);

    private final static String SITE_URL = "http://nl.storm8.com/ajax/getItemList.php?url=/equipment.php&cat=";

    private static final Pattern equipmentPattern = Pattern.compile("<table class=\"equipmentTable\"(.*?)</table>", Pattern.DOTALL);
    private static final Pattern namePattern = Pattern.compile("<div class=\"equipmentName\">(.*?)</div>");
    private static final Pattern attackPattern = Pattern.compile("<div class=\"equipmentInfoItem\">Attack: (\\d*?)</div>");
    private static final Pattern defencePattern = Pattern.compile("<div class=\"equipmentInfoItem\">Defense: (\\d*?)</div>");
    private static final Pattern upkeepPattern = Pattern.compile("Upkeep: .*?([\\d,]*?)</span>", Pattern.DOTALL);
    private static final Pattern imagePattern = Pattern.compile("http://static.storm8.com/nl/images/equipment/med/(\\d*)m.png\\?v=");

    private ObjectContainer db;
    private GameRequestor gameRequestor;

// --------------------------- CONSTRUCTORS ---------------------------

    public EquipmentAnalyzerService() {
    }

    @Inject
    public EquipmentAnalyzerService(ObjectContainer db, GameRequestor gameRequestor) {
        this.db = db;
        this.gameRequestor = gameRequestor;
    }

// -------------------------- OTHER METHODS --------------------------

    public void dig(Player player) {
        LOGGER.debug(">> dig");
        Game game = player.getGame();

        for (int cat = 1; cat <= 3; cat++) {
            String pageBuffer = gameRequestor.postRequest(SITE_URL + cat, PostBodyFactory.NULL);
            Matcher matcherEquipment = equipmentPattern.matcher(pageBuffer);
            while (MatcherHelper.isMatchFound(matcherEquipment)) {
                String equipmentInfo = matcherEquipment.group(1);

                Integer id = matchInteger(imagePattern.matcher(equipmentInfo));
                if (id == null) {
                    continue;
                }
                Equipment equipment = new Equipment(game);
                equipment.setId(id);

                if (!game.getEquipment().containsKey(id)) {
                    equipment.setName(match(namePattern.matcher(equipmentInfo)));
                    equipment.setCategory(cat);
                    equipment.setAttack(matchInteger(attackPattern.matcher(equipmentInfo)));
                    equipment.setDefence(matchInteger(defencePattern.matcher(equipmentInfo)));
                    equipment.setUpkeep(matchInteger(upkeepPattern.matcher(equipmentInfo)));

                    game.getEquipment().put(id, equipment);
                    LOGGER.debug("Item:" + equipment);
                }
            }
        }
        db.store(game);
        db.commit();
        LOGGER.debug("<< dig");
    }
}
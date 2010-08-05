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

import com.google.inject.Inject;
import net.ushkinaz.storm8.digger.MatcherHelper;
import net.ushkinaz.storm8.domain.Player;
import net.ushkinaz.storm8.domain.Victim;
import net.ushkinaz.storm8.http.GameRequestor;
import net.ushkinaz.storm8.http.PageExpiredException;
import net.ushkinaz.storm8.http.PostBodyFactory;
import net.ushkinaz.storm8.money.BankService;
import org.apache.commons.httpclient.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Dmitry Sidorenko
 */
public class HitListVisitor implements ProfileVisitor {
    @SuppressWarnings({"UnusedDeclaration"})
    private static final Logger LOGGER = LoggerFactory.getLogger(HitListVisitor.class);

//    private static final Pattern PATTERN = Pattern.compile("onclick=\"window\\.location\\.href='bounty\\.php(.*)'\"");
    private static final Pattern PATTERN = Pattern.compile("\"window\\.location\\.href\\='\\/bounty\\.php\\?(.*)'\"");

    private GameRequestor gameRequestor;
    private Player player;
    private BankService bankService;
    private static final Integer BOUNTY_VALUE = 1500;


    @Inject
    public HitListVisitor(BankService bankService, GameRequestor gameRequestor, Player player) {
        this.bankService = bankService;
        this.gameRequestor = gameRequestor;
        this.player = player;
    }

    @Override
    public void visitProfile(Victim victim, String profileHTML) throws PageExpiredException, TooManyTimesInHitListException {
        Matcher m = PATTERN.matcher(profileHTML);
        if (MatcherHelper.isMatchFound(m)) {
            String url = MatcherHelper.match(m);
            url = player.getGame().getGameURL() + "bounty.php?" + url;

            bankService.ensureHaveCash(player.getGame(), BOUNTY_VALUE);
            String res = gameRequestor.postRequest(url, new PostBodyFactory() {
                @Override
                public NameValuePair[] createBody() {
                    return new NameValuePair[]{
                            new NameValuePair("bountyValue", BOUNTY_VALUE.toString()),
                            new NameValuePair("action", "Place Bounty")
                    };
                }
            });
            if (res.contains("Failure")) {
                LOGGER.warn("Failed hitlisting");
                if (res.contains("on the hit list too many times today")) {
                    LOGGER.warn(victim.getName() + ": on the hit list too many times today");
                    throw new TooManyTimesInHitListException(victim);
                }
            } else {
                LOGGER.info("Placed bounty of " + BOUNTY_VALUE + " on " + victim.getName());
            }

        }
    }
}

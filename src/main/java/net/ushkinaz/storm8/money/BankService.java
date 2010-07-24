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

package net.ushkinaz.storm8.money;

import com.google.inject.Inject;
import net.ushkinaz.storm8.domain.Game;
import net.ushkinaz.storm8.http.GameRequestor;
import net.ushkinaz.storm8.http.PostBodyFactory;
import org.apache.commons.httpclient.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Dmitry Sidorenko
 */
public class BankService {
    @SuppressWarnings({"UnusedDeclaration"})
    private static final Logger LOGGER = LoggerFactory.getLogger(BankService.class);

    private static final String URL = "bank.php";
    private static final Pattern amountPattern = Pattern.compile("name=\"depositAmount\" value=\"(\\d*?)\"");
    private static final Pattern ballancePattern = Pattern.compile("Bank Balance:.*?padding-right:2px\">(.*?)</span>");
    private static final Pattern nextPattern = Pattern.compile(">(\\d*?):(\\d*?)</span></div></a><div class=\"levelTopArea\">");
    //

    private GameRequestor gameRequestor;

    @Inject
    public BankService(GameRequestor gameRequestor) {
        this.gameRequestor = gameRequestor;
    }

    public void ensureHaveCash(Game game, int amount) {
        String cash = getCash(gameRequestor.postRequest(game.getGameURL() + URL, PostBodyFactory.NULL));
        int intCash = 0;
        try {
            intCash = Integer.parseInt(cash);
        } catch (NumberFormatException e) {
            LOGGER.warn("Cash parsing", e);
        }
        if (intCash < amount) {
            withdraw(game, amount - intCash);
        }
    }

    public void withdraw(Game game, final int amount) {
        String result = gameRequestor.postRequest(game.getGameURL() + URL, new PostBodyFactory() {
            @Override
            public NameValuePair[] createBody() {
                return new NameValuePair[]{
                        new NameValuePair("withdrawAmount", Integer.toString(amount)),
                        new NameValuePair("action", "Withdraw"),
                        new NameValuePair("sk", "1"),
                };
            }
        });
        if (result.contains("You successfully withdrew")) {
            LOGGER.info("Withdrew: " + amount);
        }
    }

    public int putAllMoneyInBank(Game game) {
        // successfully deposited
        String bankHTML = gameRequestor.postRequest(game.getGameURL() + URL, PostBodyFactory.NULL);

        checkBalance(bankHTML);
        final String cashString = getCash(bankHTML);
        String result = gameRequestor.postRequest(game.getGameURL() + URL, new PostBodyFactory() {
            @Override
            public NameValuePair[] createBody() {
                return new NameValuePair[]{
                        new NameValuePair("depositAmount", cashString),
                        new NameValuePair("action", "Deposit"),
                        new NameValuePair("sk", "1"),
                };
            }
        });
        if (result.contains("You successfully deposited")) {
            LOGGER.info("Deposited: " + cashString);
        }
        return nextIncome(bankHTML);
    }

    private String getCash(CharSequence bankHTML) {
        Matcher matcherCodes = amountPattern.matcher(bankHTML);
        String cashString = "";
        if (matcherCodes.find()) {
            cashString = matcherCodes.group(1);
            LOGGER.info("Cash: " + cashString);
        }
        return cashString;
    }

    private void checkBalance(String bankHTML) {
        Matcher balanceMatcher = ballancePattern.matcher(bankHTML);
        if (balanceMatcher.find()) {
            final String balanceString = balanceMatcher.group(1);
            LOGGER.info("Balance: " + balanceString);
        }
    }

    /**
     * @param bankHTML
     * @return Milliseconds until next income
     */
    private int nextIncome(String bankHTML) {
        Matcher nextMatcher = nextPattern.matcher(bankHTML);
        int nextIncome = 0;
        if (nextMatcher.find()) {
            final String nextMinutes = nextMatcher.group(1);
            final String nextSeconds = nextMatcher.group(2);
            LOGGER.info("Next income: " + nextMinutes + ":" + nextSeconds);
            nextIncome = (Integer.valueOf(nextMinutes) * 60 + Integer.valueOf(nextSeconds)) * 1000;
        }
        return nextIncome;
    }
}

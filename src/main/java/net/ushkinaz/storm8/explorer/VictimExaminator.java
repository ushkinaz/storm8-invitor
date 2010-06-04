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

import net.ushkinaz.storm8.domain.Victim;
import net.ushkinaz.storm8.http.PageExpiredException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.ushkinaz.storm8.digger.MatcherHelper.isMatchFound;
import static net.ushkinaz.storm8.digger.MatcherHelper.matchInteger;

public class VictimExaminator implements ProfileVisitor {
// ------------------------------ FIELDS ------------------------------

    @SuppressWarnings({"UnusedDeclaration"})
    private static final Logger LOGGER = LoggerFactory.getLogger(VictimExaminator.class);

    static final Pattern itemPattern = Pattern.compile("src=\"http://static\\.storm8\\.com/nl/images/equipment/med/(\\d*)m\\.png\\?v=\\d*\"></div>.*?<div>x(\\d*)</div>", Pattern.DOTALL);

// --------------------------- CONSTRUCTORS ---------------------------

    public VictimExaminator() {
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface ProfileVisitor ---------------------

    @Override
    public void visitProfile(Victim victim, String profileHTML) throws PageExpiredException {
        inventory(victim, profileHTML);
    }

// -------------------------- OTHER METHODS --------------------------

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
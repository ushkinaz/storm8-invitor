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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Dmitry Sidorenko
 */
public class ClanScanner extends VictimsScanner {
// ------------------------------ FIELDS ------------------------------

    @SuppressWarnings({"UnusedDeclaration"})
    private static final Logger LOGGER = LoggerFactory.getLogger(ClanScanner.class);

    private static final String LIST_URL = "group_member.php?groupMemberRange=";

    private int scanFromIndex;
    private String clanURLBase;

// --------------------------- CONSTRUCTORS ---------------------------

    public ClanScanner() {
        scanFromIndex = 0;
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

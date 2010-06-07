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
import net.ushkinaz.storm8.digger.DBStoringCallbackFactory;
import net.ushkinaz.storm8.digger.PageDigger;
import net.ushkinaz.storm8.domain.ClanInviteSource;
import net.ushkinaz.storm8.domain.Player;
import net.ushkinaz.storm8.http.GameRequestor;
import net.ushkinaz.storm8.http.PostBodyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Dmitry Sidorenko
 */
public class BroadcastsScanner {
// ------------------------------ FIELDS ------------------------------

    @SuppressWarnings({"UnusedDeclaration"})
    private static final Logger LOGGER = LoggerFactory.getLogger(BroadcastsScanner.class);

    private PageDigger pageDigger;
    private GameRequestor gameRequestor;
    private Player player;
    private int maximumScans;
    private DBStoringCallbackFactory callbackFactory;

// --------------------------- CONSTRUCTORS ---------------------------

    @Inject
    public BroadcastsScanner(PageDigger pageDigger, GameRequestor gameRequestor, Player player, DBStoringCallbackFactory callbackFactory) {
        this.pageDigger = pageDigger;
        this.gameRequestor = gameRequestor;
        this.player = player;
        this.callbackFactory = callbackFactory;
    }


// --------------------- GETTER / SETTER METHODS ---------------------

    public int getMaximumScans() {
        return maximumScans;
    }

    public void setMaximumScans(int maximumScans) {
        this.maximumScans = maximumScans;
    }

    protected Player getPlayer() {
        return player;
    }

// -------------------------- OTHER METHODS --------------------------

    public void digCodes() {
        String requestURL = player.getGame().getGameURL() + "ajax/getNewsFeedStories.php?selectedTab=broadcasts";
        String body = gameRequestor.postRequest(requestURL, PostBodyFactory.NULL);
        pageDigger.parsePost(body, callbackFactory.get(getPlayer().getGame(), ClanInviteSource.INGAME_BROADCAST));
    }
}
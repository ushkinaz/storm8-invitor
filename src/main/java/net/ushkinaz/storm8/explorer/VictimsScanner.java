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
import net.ushkinaz.storm8.domain.Player;
import net.ushkinaz.storm8.domain.Victim;
import net.ushkinaz.storm8.http.GameRequestor;
import net.ushkinaz.storm8.http.PageExpiredException;
import net.ushkinaz.storm8.http.PostBodyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.ushkinaz.storm8.digger.MatcherHelper.*;

/**
 * @author Dmitry Sidorenko
 */
public abstract class VictimsScanner {
// ------------------------------ FIELDS ------------------------------

    @SuppressWarnings({"UnusedDeclaration"})
    private static final Logger LOGGER = LoggerFactory.getLogger(VictimsScanner.class);

    private static final Pattern profilePattern = Pattern.compile("<a href=\"/profile\\.php\\?puid=(\\d*)&(.*?)\">(.*?)</a>");
    private GameRequestor gameRequestor;
    private Player player;
    private ObjectContainer db;
    private int maximumVictims;
    private int victimsVisited;

// --------------------------- CONSTRUCTORS ---------------------------

    protected VictimsScanner() {
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    protected Player getPlayer() {
        return player;
    }

    @Inject
    public void setPlayer(Player player) {
        this.player = player;
    }

    @Inject
    public void setDb(ObjectContainer db) {
        this.db = db;
    }

    @Inject
    public void setGameRequestor(GameRequestor gameRequestor) {
        this.gameRequestor = gameRequestor;
    }

// -------------------------- OTHER METHODS --------------------------

    public void visitVictims(ProfileVisitor... profileVisitors) {
        LOGGER.debug(">> visitVictims");
        scanVictims(profileVisitors);
        LOGGER.debug("<< visitVictims");
    }

    /**
     * Scans single page of clan members.
     * Since links on that page will often expire, a page will be reloaded starting at expired link, effectively moving down the list.
     * So, in fact, this method will scan the whole clan.
     *
     * @param profileVisitors visitors to use
     */
    private void scanVictims(ProfileVisitor... profileVisitors) {
        String requestURL = getListURL();
        String body = gameRequestor.postRequest(requestURL, PostBodyFactory.NULL);
        Matcher matcher = profilePattern.matcher(body);
        while (isMatchFound(matcher)) {
            int puid = matchInteger(matcher);
            String timeStamp = match(matcher, 2);
            String name = match(matcher, 3);
            String profileURL = String.format("%sprofile.php?puid=%s&%s", player.getGame().getGameURL(), puid, timeStamp);
            LOGGER.debug(profileURL);

            try {
                String profileHTML = gameRequestor.postRequest(profileURL, PostBodyFactory.NULL);

                Victim victim = new Victim(puid, player.getGame());
                List<Victim> victims = db.queryByExample(victim);
                victim.setName(name);
                if (victims.size() > 0) {
                    victim = victims.get(0);
                }
                for (ProfileVisitor visitor : profileVisitors) {
                    visitor.visitProfile(victim, profileHTML);
                }
                profileVisited(victim);
                db.store(victim);
                db.commit();
                victimsVisited++;

                LOGGER.debug("Scanned: " + victimsVisited);
                if (maximumVictims > 0 && victimsVisited >= maximumVictims) {
                    LOGGER.debug("Maximum Victims reached");
                    return;
                }
            } catch (PageExpiredException e) {
                LOGGER.debug("Restarting scan, time stamp expired");
                scanVictims(profileVisitors);
                break;
            }
        }
    }

    protected abstract String getListURL();

    /**
     * Called when we successfully visited a profile
     *
     * @param victim victim we visited
     */
    protected abstract void profileVisited(Victim victim);

    public void setMaximumVictims(int maximumVictims) {
        this.maximumVictims = maximumVictims;
    }

    public int getMaximumVictims() {
        return maximumVictims;
    }
}

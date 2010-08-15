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

package net.ushkinaz.storm8;

import com.db4o.ObjectContainer;
import com.db4o.config.ConfigScope;
import com.db4o.defragment.Defragment;
import com.db4o.defragment.DefragmentConfig;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import net.ushkinaz.storm8.digger.CodesDigger;
import net.ushkinaz.storm8.digger.LiveCodesDigger;
import net.ushkinaz.storm8.digger.annotations.ByName;
import net.ushkinaz.storm8.digger.annotations.Comments;
import net.ushkinaz.storm8.digger.annotations.FightList;
import net.ushkinaz.storm8.digger.annotations.HitList;
import net.ushkinaz.storm8.digger.forum.ForumCodesDigger;
import net.ushkinaz.storm8.domain.ClanInvite;
import net.ushkinaz.storm8.domain.Configuration;
import net.ushkinaz.storm8.domain.Game;
import net.ushkinaz.storm8.domain.Player;
import net.ushkinaz.storm8.explorer.*;
import net.ushkinaz.storm8.guice.PlayerProvider;
import net.ushkinaz.storm8.guice.Storm8Module;
import net.ushkinaz.storm8.invite.InviteService;
import net.ushkinaz.storm8.money.BankService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;


public class StormMe {
// ------------------------------ FIELDS ------------------------------

    @SuppressWarnings({"UnusedDeclaration"})
    private static final Logger LOGGER = LoggerFactory.getLogger(StormMe.class);
    private static final String STORM_DB = "storm8.db";

    private Configuration configuration;
    private Injector injector;

// --------------------------- CONSTRUCTORS ---------------------------

    @Inject
    public StormMe(Configuration configuration, Injector injector) {
        this.configuration = configuration;
        this.injector = injector;
    }

// --------------------------- main() method ---------------------------

    public static void main(String[] args) throws Exception {
        Set<String> arguments = new HashSet<String>(Arrays.asList(args));

        if (arguments.contains("defragment")) {
            new File("storm8.db.backup").delete();
            LOGGER.info("Defragmenting");
            DefragmentConfig defragmentConfig = new DefragmentConfig(STORM_DB);
            defragmentConfig.db4oConfig().generateUUIDs(ConfigScope.GLOBALLY);
            defragmentConfig.db4oConfig().generateVersionNumbers(ConfigScope.GLOBALLY);
            Defragment.defrag(defragmentConfig);
        }

        final Storm8Module storm8Module = new Storm8Module(STORM_DB);
        Injector injector = Guice.createInjector(storm8Module);

        StormMe stormMe = injector.getInstance(StormMe.class);

        LOGGER.debug("Parsing cli");

        if (arguments.contains("clean")) {
            ObjectContainer db = injector.getInstance(ObjectContainer.class);
            for (Object o : db.queryByExample(Game.class)) {
                db.delete(o);
            }
            for (Object o : db.queryByExample(ClanInvite.class)) {
                db.delete(o);
            }
            db.commit();
        }

        if (arguments.contains("hitlist-coment")) {
            stormMe.hitlist();
        }

        if (arguments.contains("inventory")) {
            stormMe.inventory();
        }

        if (arguments.contains("batch")) {
            stormMe.batch();
        }

        if (arguments.contains("dig")) {
            stormMe.digSites();
        }

        if (arguments.contains("dig-broadcasts")) {
            stormMe.digBroadcasts();
        }

        if (arguments.contains("dig-comments")) {
            stormMe.digComments();
        }

        if (arguments.contains("post-comments")) {
            stormMe.postComment();
        }

        if (arguments.contains("invite")) {
            stormMe.invite();
        }

        if (arguments.contains("bank-money")) {
            stormMe.bankMoney();
        }
    }

    private void hitlist() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Player player = configuration.getPlayer("ush-ninja");
                injector.getInstance(PlayerProvider.class).setPlayer(player);

                VictimScanFilter victimFilter = injector.getInstance(Key.get(VictimScanFilter.class, ByName.class));
                VictimsScanner victimsScanner = injector.getInstance(Key.get(VictimsScanner.class, Comments.class));
                victimsScanner.setVictimFilter(victimFilter);
                ProfileVisitor hitListVisitor = injector.getInstance(HitListVisitor.class);

                final Random random = new Random();
                boolean doTheJob = true;
                while (doTheJob) {
                    try {
                        victimsScanner.visitVictims(hitListVisitor);
                        //Minimal - once in 10 secs

                        //Once in 10 mins + random
                        final int pause = getNextPause(random);
                        LOGGER.info("Sleeping for " + pause + " milliseconds");
                        Thread.sleep(pause);
                    } catch (InterruptedException e) {
                        doTheJob = false;
                        LOGGER.error("Interrupted", e);
                    } catch (StopVisitingException e) {
                        final int waitFor = 3600000;
                        LOGGER.warn("Stop visiting, waiting for " + waitFor);
                        sleepFor(waitFor);
                    }
                }
                LOGGER.info("Hitlist job finished");
            }
        }, "HitListing").start();
    }

    private void sleepFor(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            LOGGER.warn("Interrupted", e);
        }
    }

    private int getNextPause(Random random) {
//        return 10000;
        return 300000 + random.nextInt(300000);
    }

    private void batch() {
        digBroadcasts();
        digSites();
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean exitFlag = false;
                while (!exitFlag) {
                    digComments();
                    invite();
                    try {
                        Thread.sleep(1000 * 60 * 5);
                    } catch (InterruptedException e) {
                        exitFlag = true;
                    }
                }
            }
        }, "Batcher").start();
    }

    private void digBroadcasts() {
        Player player = configuration.getPlayer("ush-ninja");
        injector.getInstance(PlayerProvider.class).setPlayer(player);
        BroadcastsScanner broadcastsDigger = injector.getInstance(BroadcastsScanner.class);
        broadcastsDigger.digCodes();
    }

    private void bankMoney() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Player player = configuration.getPlayer("ush-ninja");
                injector.getInstance(PlayerProvider.class).setPlayer(player);

                BankService bankService = injector.getInstance(BankService.class);
                boolean doTheJob = true;
                while (doTheJob) {
                    try {
                        //Doing it twice to be sure we didn't miss the time
//                bankService.putAllMoneyInBank(player.getGame());
//                Thread.sleep(5000);
                        int nextIncome = bankService.putAllMoneyInBank(player.getGame());
                        LOGGER.info("Sleeping for " + nextIncome + " milliseconds");
                        Thread.sleep(nextIncome);
                    } catch (InterruptedException e) {
                        doTheJob = false;
                        LOGGER.error("Interrupted", e);
                    }
                }
            }
        }, "Banking").start();
    }

    private void inventory() {
        Player player = configuration.getPlayer("ush-ninja");

        EquipmentAnalyzerService equipmentAnalyzerService = injector.getInstance(EquipmentAnalyzerService.class);
        equipmentAnalyzerService.dig(player);
    }

    private void digSites() {
        new Thread("SitesDigger") {
            @Override
            public void run() {
                boolean exitFlag = false;
                while (!exitFlag) {
                    Game game = configuration.getGame("ninja");

                    CodesDigger forumDigger = injector.getInstance(ForumCodesDigger.class);
                    forumDigger.digCodes(game);

                    CodesDigger liveCodesDigger = injector.getInstance(LiveCodesDigger.class);
                    liveCodesDigger.digCodes(game);
                    try {
                        sleep(1000 * 60 * 30);
                    } catch (InterruptedException e) {
                        exitFlag = true;
                    }
                }
            }
        }.start();
    }

    private void digComments() {
        try {
            Player player = configuration.getPlayer("ush-ninja");
            injector.getInstance(PlayerProvider.class).setPlayer(player);
            ProfileCommentsVisitor profileCommentsVisitor;
            profileCommentsVisitor = injector.getInstance(ProfileCodesDiggerVisitor.class);

/*
            VictimsScanner victimsScanner = injector.getInstance(Key.get(VictimsScanner.class, Clan.class));
            victimsScanner.visitVictims(profileCommentsVisitor);
*/

            VictimsScanner hitListScanner = injector.getInstance(Key.get(VictimsScanner.class, HitList.class));
            hitListScanner.setScanVictims(500);
            hitListScanner.visitVictims(profileCommentsVisitor);

            VictimsScanner fightsScanner = injector.getInstance(Key.get(VictimsScanner.class, FightList.class));
            fightsScanner.setScanVictims(1000);
            fightsScanner.visitVictims(profileCommentsVisitor);
        } catch (StopVisitingException e) {
            LOGGER.error("Error", e);
        }
    }

    private void postComment() {
        try {
            Player player = configuration.getPlayer("ush-ninja");
            injector.getInstance(PlayerProvider.class).setPlayer(player);
            ProfileCommentsVisitor postCodeVisitor = injector.getInstance(ProfilePostCodeVisitor.class);

            VictimsScanner victimsScanner = injector.getInstance(Key.get(VictimsScanner.class, FightList.class));
            victimsScanner.setScanVictims(2000);
            victimsScanner.visitVictims(postCodeVisitor);
        } catch (StopVisitingException e) {
            LOGGER.error("Error", e);
        }

//        VictimsScanner hitListScanner = injector.getInstance(Key.get(VictimsScanner.class, HitList.class));
//        hitListScanner.visitVictims(postCodeVisitor);
    }

    private void invite() {
        Player player = configuration.getPlayer("ush-ninja");
        injector.getInstance(PlayerProvider.class).setPlayer(player);
        InviteService service = injector.getInstance(InviteService.class);
        service.invite(player);

    }
}
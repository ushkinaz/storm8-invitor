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
import net.ushkinaz.storm8.digger.annotations.*;
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

        if (arguments.contains("scan-targets")) {
            stormMe.scanTargets();
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
                bankMoney();
            }
        }).start();
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
                        Thread.sleep(getNextPause(random));
                    } catch (InterruptedException e) {
                        doTheJob = false;
                        LOGGER.error("Interrupted", e);
                        break;
                    }
                }
            }
        }).start();
    }

    private int getNextPause(Random random) {
        return 600000 + random.nextInt(300000);
    }

    private void batch() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                bankMoney();
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    digBroadcasts();
                    digComments();
                    digSites();
                    invite();
/*
                    try {
                        Thread.sleep(1000 * 60 * 60);
                    } catch (InterruptedException e) {
                        //TODO: add proper handling
                        LOGGER.error("Error", e);
                    }
*/
                }
            }
        }).start();
    }

    private void digBroadcasts() {
        Player player = configuration.getPlayer("ush-ninja");
        injector.getInstance(PlayerProvider.class).setPlayer(player);
        BroadcastsScanner broadcastsDigger = injector.getInstance(BroadcastsScanner.class);
        broadcastsDigger.digCodes();
        broadcastsDigger.setMaximumScans(1);
    }

    private void bankMoney() {
        Player player = configuration.getPlayer("ush-ninja");
        injector.getInstance(PlayerProvider.class).setPlayer(player);

        BankService bankService = injector.getInstance(BankService.class);
        boolean doTheJob = true;
        while (doTheJob) {
            try {
                //Doing it twice to be sure we didn't miss the time
                bankService.putAllMoneyInBank(player.getGame());
                Thread.sleep(5000);
                int nextIncome = bankService.putAllMoneyInBank(player.getGame());
                LOGGER.info("Sleeping for " + nextIncome + " milliseconds");
                Thread.sleep(nextIncome);
            } catch (InterruptedException e) {
                doTheJob = false;
                LOGGER.error("Interrupted", e);
                break;
            }
        }
    }

    private void inventory() {
        Player player = configuration.getPlayer("ush-ninja");

        EquipmentAnalyzerService equipmentAnalyzerService = injector.getInstance(EquipmentAnalyzerService.class);
        equipmentAnalyzerService.dig(player);
    }

    private void scanTargets() {
    }

    private void digSites() {
        Game game = configuration.getGame("ninja");

        CodesDigger forumDigger = injector.getInstance(ForumCodesDigger.class);
        forumDigger.digCodes(game);

        CodesDigger liveCodesDigger = injector.getInstance(LiveCodesDigger.class);
        liveCodesDigger.digCodes(game);
    }

    private void digComments() {
        Player player = configuration.getPlayer("ush-ninja");
        injector.getInstance(PlayerProvider.class).setPlayer(player);
        ProfileCommentsVisitor profileCommentsVisitor = injector.getInstance(ProfileCodesDiggerVisitor.class);

        VictimsScanner victimsScanner = injector.getInstance(Key.get(VictimsScanner.class, Clan.class));
        victimsScanner.visitVictims(profileCommentsVisitor);

        VictimsScanner hitListScanner = injector.getInstance(Key.get(VictimsScanner.class, HitList.class));
        hitListScanner.setMaximumVictims(1000);
        hitListScanner.visitVictims(profileCommentsVisitor);

        VictimsScanner fightsScanner = injector.getInstance(Key.get(VictimsScanner.class, FightList.class));
        fightsScanner.setMaximumVictims(1000);
        fightsScanner.visitVictims(profileCommentsVisitor);
    }

    private void postComment() {
        Player player = configuration.getPlayer("ush-ninja");
        injector.getInstance(PlayerProvider.class).setPlayer(player);
        ProfileCommentsVisitor postCodeVisitor = injector.getInstance(ProfilePostCodeVisitor.class);

        VictimsScanner victimsScanner = injector.getInstance(Key.get(VictimsScanner.class, FightList.class));
        victimsScanner.setMaximumVictims(2000);
        victimsScanner.visitVictims(postCodeVisitor);

//        VictimsScanner hitListScanner = injector.getInstance(Key.get(VictimsScanner.class, HitList.class));
//        hitListScanner.visitVictims(postCodeVisitor);
    }

    private void invite() {
        Player player = configuration.getPlayer("ush-ninja");
        injector.getInstance(PlayerProvider.class).setPlayer(player);
        InviteService service = injector.getInstance(InviteService.class);
        service.invite(player);

/*
        for (Game game : configurator.getGames().values()) {
            service.invite(game);
        }
*/
    }
}
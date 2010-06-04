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

package net.ushkinaz.storm8.digger.forum;

import com.db4o.ObjectContainer;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.ushkinaz.storm8.digger.CodesDigger;
import net.ushkinaz.storm8.digger.DBStoringCallbackFactory;
import net.ushkinaz.storm8.domain.ClanInviteSource;
import net.ushkinaz.storm8.domain.Game;
import net.ushkinaz.storm8.domain.Topic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Singleton
public class ForumCodesDigger implements CodesDigger {
// ------------------------------ FIELDS ------------------------------

    private static final Logger LOGGER = LoggerFactory.getLogger(ForumCodesDigger.class);

    private TopicAnalyzerService topicAnalyzerService;
    private ForumAnalyzerService forumAnalyzerService;
    private ObjectContainer db;
    private DBStoringCallbackFactory callbackFactory;

// --------------------------- CONSTRUCTORS ---------------------------

    @Inject
    public ForumCodesDigger(DBStoringCallbackFactory callbackFactory, ObjectContainer db, ForumAnalyzerService forumAnalyzerService, TopicAnalyzerService topicAnalyzerService) {
        this.callbackFactory = callbackFactory;
        this.db = db;
        this.forumAnalyzerService = forumAnalyzerService;
        this.topicAnalyzerService = topicAnalyzerService;
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface CodesDigger ---------------------

    public void digCodes(final Game game) {
        LOGGER.debug(">> digCodes");
        forumAnalyzerService.findTopics(game);
        db.store(game);
        db.commit();

        ExecutorService executor = new ThreadPoolExecutor(5, 15, 120, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        for (final Topic topic : game.getTopics().values()) {
            if (!topic.arePostsAdded()) {
                LOGGER.debug("No new posts: " + topic);
                continue;
            }

            executor.execute(new Runnable() {
                @Override
                public void run() {
                    topicAnalyzerService.searchForCodes(topic, callbackFactory.get(game, ClanInviteSource.FORUM));
                    db.store(topic);
                    db.commit();
                }
            });
        }

        try {
            executor.shutdown();
            if (!executor.awaitTermination(15, TimeUnit.MINUTES)) {
                LOGGER.warn("Too long operation");
            }
        } catch (InterruptedException e) {
            LOGGER.error("Error", e);
        }
//        //Store updated topics list
        db.store(game);
        db.commit();
        LOGGER.debug("<< digCodes");
    }
}
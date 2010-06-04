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

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import net.ushkinaz.storm8.domain.Configuration;
import net.ushkinaz.storm8.domain.Player;
import net.ushkinaz.storm8.guice.Storm8Module;
import net.ushkinaz.storm8.http.GameRequestor;
import net.ushkinaz.storm8.http.PostBodyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


public class ExploreMe {
// ------------------------------ FIELDS ------------------------------

    @SuppressWarnings({"UnusedDeclaration"})
    private static final Logger LOGGER = LoggerFactory.getLogger(ExploreMe.class);

    String[] uris = {
            "/ajax/getNewsFeedStories.php?selectedTab=fight",
            "/profile.php?puid=2688908",
            "/fight.php",
            "/profile.php?puid=2305363&formNonce=6d7de5664fd0ccd08b38702c1067ee43d04ad92f&h=80724bfb34f35bfc41af5cdcda1d81ed3b53278f",
            "/group.php",
    };

    private Configuration configuration;
    private GameRequestor requestor;

// -------------------------- OTHER METHODS --------------------------

    @Inject
    public void ExploreMe(Configuration configuration, GameRequestor gameRequestor) {
        this.requestor = gameRequestor;
        this.configuration = configuration;
    }

// --------------------------- main() method ---------------------------

    public static void main(String[] args) throws Exception {
        final Storm8Module storm8Module = new Storm8Module("storm8.db");
        Injector injector = Guice.createInjector(storm8Module);

        ExploreMe exploreMe = injector.getInstance(ExploreMe.class);

        exploreMe.doIt();
    }

    private void doIt() throws IOException {
        Player ninja = configuration.getPlayer("ush-ninja");
        for (String uri : uris) {
            String body = requestor.postRequest(ninja.getGame().getGameURL() + uri, PostBodyFactory.NULL);
            logResponse(body, uri);
        }
    }

    private void logResponse(String response, String request) {
        PrintWriter writer = null;

        try {
            String fileName = "responses/" + request.replace("?", "_").replace("/", "-") + ".html";
            writer = new PrintWriter(new FileWriter(fileName));
            writer.write(response);
            LOGGER.info("Response is written to " + fileName);
        } catch (IOException e) {
            LOGGER.error("Error", e);
        } finally {
            assert writer != null;
            writer.close();
        }
    }
}
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

import com.google.inject.Singleton;
import net.ushkinaz.storm8.domain.Game;
import net.ushkinaz.storm8.domain.Topic;
import net.ushkinaz.storm8.http.HttpHelper;
import net.ushkinaz.storm8.http.HttpService;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Date: 23.05.2010
 * Created by Dmitry Sidorenko.
 */
@Singleton
public class ForumAnalyzerService extends HttpService {
// ------------------------------ FIELDS ------------------------------

    private static final Logger LOGGER = LoggerFactory.getLogger(ForumAnalyzerService.class);

    private final static String FORUM_URL = "http://forums.storm8.com/forumdisplay.php?f={0,number,######}";

    private static final Pattern topicPattern = Pattern.compile("t=(\\d*)\\&amp;page=(\\d*)\"\\>Last Page");

// --------------------------- CONSTRUCTORS ---------------------------

    public ForumAnalyzerService() {
    }

// -------------------------- OTHER METHODS --------------------------

    public void findTopics(Game game) {
        LOGGER.info("Forum: " + game.getForumId());
        parseTopics(game);
    }

    private void parseTopics(Game game) {
        GetMethod pagesMethod = new GetMethod(MessageFormat.format(FORUM_URL, game.getForumId()));
        String page = HttpHelper.getHttpResponse(getClient(), pagesMethod);
        Matcher matcher = topicPattern.matcher(page);
        while (matcher.find()) {
            int topicId = Integer.parseInt(matcher.group(1));
            int lastPage = Integer.parseInt(matcher.group(2));
            LOGGER.debug("Found topic: " + topicId);

            Pattern postsPattern = Pattern.compile("t=" + topicId + "\" onclick=\"who\\(\\d*\\); return false;\">([\\d,]*)<");
            Matcher postsMatcher = postsPattern.matcher(page);
            int posts = 0;
            if (postsMatcher.find()) {
                String postsString = postsMatcher.group(1).replace(",", "");
                posts = Integer.parseInt(postsString);
            }

            Topic topic;

            if (game.getTopics().containsKey(topicId)) {
                topic = game.getTopics().get(topicId);
            } else {
                topic = new Topic(topicId);
            }

            topic.setPages(lastPage);
            int oldPosts = topic.getPosts();
            if (oldPosts != posts) {
                topic.setPosts(posts);
                topic.setPostsAdded(true);
            }
            game.getTopics().put(topicId, topic);
        }
    }
}
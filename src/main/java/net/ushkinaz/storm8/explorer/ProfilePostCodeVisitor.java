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
import net.ushkinaz.storm8.digger.PageDigger;
import net.ushkinaz.storm8.http.PostBodyFactory;
import org.apache.commons.httpclient.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.ushkinaz.storm8.digger.MatcherHelper.isMatchFound;
import static net.ushkinaz.storm8.digger.MatcherHelper.match;

/**
 * @author Dmitry Sidorenko
 */
public class ProfilePostCodeVisitor extends ProfileCommentsVisitor {
// ------------------------------ FIELDS ------------------------------

    @SuppressWarnings({"UnusedDeclaration"})
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfilePostCodeVisitor.class);

    private static final Pattern postCommentPattern = Pattern.compile("action=\"/(.*?)\">");

    private BlackListEvaluator blackListEvaluator;

// --------------------------- CONSTRUCTORS ---------------------------

    @Inject
    public ProfilePostCodeVisitor(BlackListEvaluator blackListEvaluator) {
        this.blackListEvaluator = blackListEvaluator;
    }

// -------------------------- OTHER METHODS --------------------------

    @Override
    protected void handleComments(PageDigger.CodesDiggerCallback callback, String commentsBody) {
        final String commentText = "Please add this guy " + getPlayer().getCode();

        if (!blackListEvaluator.canPost(commentsBody, commentText)) {
            return;
        }

        Matcher postForm = postCommentPattern.matcher(commentsBody);
        while (isMatchFound(postForm)) {
            String url = getPlayer().getGame().getGameURL() + match(postForm);
            String result = getGameRequestor().postRequest(url, new PostBodyFactory() {
                @Override
                public NameValuePair[] createBody() {
                    return new NameValuePair[]{
                            new NameValuePair("commentText", commentText),
                            new NameValuePair("action", "Post Comment")
                    };
                }
            });
            if (result.contains("Your message contains inappropriate word(s).  Please enter a new message.")) {
                throw new IllegalStateException("Failure: Your message contains inappropriate word(s): " + commentText);
            }
        }
        LOGGER.debug("Code posted");
    }
}
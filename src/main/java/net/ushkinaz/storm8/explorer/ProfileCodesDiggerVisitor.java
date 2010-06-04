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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.ushkinaz.storm8.digger.MatcherHelper.isMatchFound;
import static net.ushkinaz.storm8.digger.MatcherHelper.match;

/**
 * @author Dmitry Sidorenko
 */
public class ProfileCodesDiggerVisitor extends ProfileCommentsVisitor {
// ------------------------------ FIELDS ------------------------------

    @SuppressWarnings({"UnusedDeclaration"})
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileCodesDiggerVisitor.class);

    private static final Pattern commentPattern = Pattern.compile("<div style=\"font-weight: bold; width: 250px\">(.*?)</div>", Pattern.DOTALL);

    private PageDigger digger;

// --------------------------- CONSTRUCTORS ---------------------------

    @Inject
    public ProfileCodesDiggerVisitor(PageDigger digger) {
        this.digger = digger;
    }

// -------------------------- OTHER METHODS --------------------------

    @Override
    protected void handleComments(PageDigger.CodesDiggerCallback callback, String commentsBody) {
        Matcher posts = commentPattern.matcher(commentsBody);
        while (isMatchFound(posts)) {
            digger.parsePost(match(posts), callback);
        }
    }
}

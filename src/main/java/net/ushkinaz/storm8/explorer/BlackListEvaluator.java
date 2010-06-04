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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Dmitry Sidorenko
 * @date Jun 4, 2010
 */
public class BlackListEvaluator {
    @SuppressWarnings({"UnusedDeclaration"})
    private static final Logger LOGGER = LoggerFactory.getLogger(BlackListEvaluator.class);

    /**
     * Can we post this comment here?
     *
     * @param commentsBody comments page
     * @param commentText  comment to post
     * @return {@code true} if we can
     */
    public boolean canPost(String commentsBody, String commentText) {
        commentsBody = commentsBody.toUpperCase();

        if (commentsBody.contains(commentText.toUpperCase())) {
            LOGGER.debug("Already posted");
            return false;
        } else if (commentsBody.contains("DO NOT POST")) {
            return false;
        } else if (commentsBody.contains("DON'T POST")) {
            return false;
        } else if (commentsBody.contains("NO CODES")) {
            return false;
        } else if (commentsBody.contains("NO POST")) {
            return false;
        }

        return true;
    }
}

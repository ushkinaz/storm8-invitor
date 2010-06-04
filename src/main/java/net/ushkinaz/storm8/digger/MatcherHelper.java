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

package net.ushkinaz.storm8.digger;

import java.util.regex.Matcher;

public class MatcherHelper {
// -------------------------- STATIC METHODS --------------------------

    public static boolean isMatchFound(Matcher matcher) {
        return matcher.find();
    }

    public static int matchInteger(Matcher matcher) {
        return matchInteger(matcher, 1);
    }

    /**
     * Returns integer value from 1st group
     *
     * @param matcher
     * @param group
     * @return int
     */
    public static int matchInteger(Matcher matcher, int group) {
        String result = match(matcher, group);
        if (result == null) {
            result = "0";
        }
        return Integer.parseInt(result.replace(",", ""));
    }

    public static String match(Matcher matcher, int group) {
        return matcher.group(group);
    }

    public static String match(Matcher matcher) {
        return match(matcher, 1);
    }

// --------------------------- CONSTRUCTORS ---------------------------

    private MatcherHelper() {
    }
}
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

import com.google.inject.Inject;
import net.ushkinaz.storm8.configuration.CodesBlackList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Date: 31.05.2010
 * Created by Dmitry Sidorenko.
 */
public class PageDigger {
// ------------------------------ FIELDS ------------------------------

    private static final Logger LOGGER = LoggerFactory.getLogger(PageDigger.class);

    private static final String CODE_PATTERN = "\\w{5}";
    private Pattern codePattern = Pattern.compile("\\W(" + CODE_PATTERN + ")\\W");
    private CodesBlackList codesBlackList;

// --------------------------- CONSTRUCTORS ---------------------------

    @Inject
    public PageDigger(CodesBlackList codesBlackList) {
        this.codesBlackList = codesBlackList;
    }

// -------------------------- OTHER METHODS --------------------------

    public void parsePost(String post, CodesDiggerCallback callback) {
        Matcher matcher = codePattern.matcher(post);
        while (matcher.find()) {
            String code = matcher.group(1).toUpperCase();
            if (codesBlackList.isBlackListed(code)) {
                continue;
            }
            LOGGER.debug("Code: " + code);
            callback.codeFound(code);
        }
    }

    protected void setCodePattern(String patternString) {
        this.codePattern = Pattern.compile(patternString);
    }

// -------------------------- INNER CLASSES --------------------------

    public interface CodesDiggerCallback {
        void codeFound(String code);
    }
}

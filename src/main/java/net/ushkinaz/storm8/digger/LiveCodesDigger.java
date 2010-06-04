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
import com.google.inject.Singleton;
import net.ushkinaz.storm8.domain.ClanInviteSource;
import net.ushkinaz.storm8.domain.Game;
import net.ushkinaz.storm8.http.HttpHelper;
import net.ushkinaz.storm8.http.HttpService;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Date: 23.05.2010
 * Created by Dmitry Sidorenko.
 */
@Singleton
public class LiveCodesDigger extends HttpService implements CodesDigger {
// ------------------------------ FIELDS ------------------------------

    private static final Logger LOGGER = LoggerFactory.getLogger(LiveCodesDigger.class);

    private final static String SITE_URL = "http://getninjaslivecodes.com/";

    private static final Pattern codesPattern = Pattern.compile("User Codes(.*)\\<\\/ul\\>", Pattern.DOTALL);

    private PageDigger pageDigger;
    private DBStoringCallbackFactory callbackFactory;

// --------------------------- CONSTRUCTORS ---------------------------

    @Inject
    public LiveCodesDigger(DBStoringCallbackFactory callbackFactory, PageDigger pageDigger) {
        this.callbackFactory = callbackFactory;
        this.pageDigger = pageDigger;
        pageDigger.setCodePattern("<li>(\\w{5})</li>");
    }
    // ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface CodesDigger ---------------------

    @Override
    public void digCodes(Game game) {
        LOGGER.debug(">> digCodes");
        Matcher matcherCodes = HttpHelper.getHttpMatcher(getClient(), new GetMethod(SITE_URL), codesPattern);
        if (matcherCodes.find()) {
            String strCodes = matcherCodes.group(1);
            pageDigger.parsePost(strCodes, callbackFactory.get(game, ClanInviteSource.LIVE_CODES));
        }
        LOGGER.debug("<< digCodes");
    }

// -------------------------- OTHER METHODS --------------------------

}
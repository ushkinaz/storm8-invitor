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

package net.ushkinaz.storm8.invite;

import com.google.inject.Singleton;
import net.ushkinaz.storm8.domain.ClanInvite;
import net.ushkinaz.storm8.domain.ClanInviteStatus;
import net.ushkinaz.storm8.http.ServerWorkflowException;
import org.slf4j.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.slf4j.LoggerFactory.getLogger;

@Singleton
public class InviteParser {
// ------------------------------ FIELDS ------------------------------

    private static final Logger LOGGER = getLogger(InviteParser.class);

    private static final String NAME_PATTERN = "(.*?)";

    //private final ClanDao clanDao;

    private static Pattern successPattern = Pattern.compile(".*<div class=\"messageBoxSuccess\"><span class=\"success\">Success!</span> You invited " + NAME_PATTERN + " to your clan.</div>.*", Pattern.DOTALL);
    private static Pattern alreadyInvitedPattern = Pattern.compile(".*<div class=\"messageBoxFail\"><span class=\"fail\">Failure:</span> You already invited " + NAME_PATTERN + " to join your clan.</div>.*", Pattern.DOTALL);
    private static Pattern inClanPattern = Pattern.compile(".*<div class=\"messageBoxFail\"><span class=\"fail\">Failure:</span> " + NAME_PATTERN + " is already in your clan.</div>.*", Pattern.DOTALL);
    private static Pattern notFoundPattern = Pattern.compile(".*<div class=\"messageBoxFail\"><span class=\"fail\">Failure:</span> Unable to find anyone with that clan code.</div>.*", Pattern.DOTALL);
    private static Pattern notAndroidPattern = Pattern.compile(".*If you are using an Android device, press the back button to exit the game.*", Pattern.DOTALL);
    private static Pattern yourselfPattern = Pattern.compile(".*You cannot invite yourself to your own clan.*", Pattern.DOTALL);

// --------------------------- CONSTRUCTORS ---------------------------

    public InviteParser() {
    }

// -------------------------- OTHER METHODS --------------------------

    public void parseResult(String response, ClanInvite clanInvite) throws ServerWorkflowException {
        Matcher matcherSuccess = successPattern.matcher(response);
        Matcher matcherAlreadyInvited = alreadyInvitedPattern.matcher(response);
        Matcher matcherInClan = inClanPattern.matcher(response);
        Matcher matcherNotFound = notFoundPattern.matcher(response);
        Matcher matcherNotAndroid = notAndroidPattern.matcher(response);
        Matcher matcherCleverBoy = yourselfPattern.matcher(response);

        if (matcherSuccess.matches()) {
            updateInvite(clanInvite, matcherSuccess, ClanInviteStatus.PENDING);
        } else if (matcherAlreadyInvited.matches()) {
            updateInvite(clanInvite, matcherAlreadyInvited, ClanInviteStatus.PENDING);
        } else if (matcherInClan.matches()) {
            updateInvite(clanInvite, matcherInClan, ClanInviteStatus.ACCEPTED);
        } else if (matcherNotFound.matches()) {
            clanInvite.setStatus(ClanInviteStatus.NOT_FOUND);
            LOGGER.info("Unable to find clan with code: " + clanInvite.getCode());
        } else if (matcherNotAndroid.matches()) {
            LOGGER.error("Server does not like us!");
            throw new ServerWorkflowException("Server does not like us!\n" + response);
        } else if (matcherCleverBoy.matches()) {
            LOGGER.warn("Adding yourself? Clever boy!");
            clanInvite.setStatus(ClanInviteStatus.ACCEPTED);
        } else {
            LOGGER.warn("Unknown error");
            logUnknownError(response, clanInvite);
        }
    }

    private void updateInvite(ClanInvite clanInvite, Matcher matcher, ClanInviteStatus status) {
        String clanName;
        try {
            clanName = matcher.group(1);
            clanInvite.setName(clanName);
            clanInvite.setStatus(status);
            LOGGER.debug(status + ":" + clanName);
        } catch (IllegalStateException e) {
            LOGGER.error(matcher.toString());
            LOGGER.error("match problem", e);
        }
    }

    private void logUnknownError(String response, ClanInvite clanInvite) {
        PrintWriter writer = null;
        try {
            String fileName = clanInvite.getCode() + ".resp";
            writer = new PrintWriter(new FileWriter(fileName));
            writer.write(response);
            LOGGER.warn("Response is written to " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            assert writer != null;
            writer.close();
        }
    }
}
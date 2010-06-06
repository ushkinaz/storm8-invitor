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

package net.ushkinaz.storm8.configuration;

import com.db4o.ObjectContainer;
import com.db4o.query.Query;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import javolution.util.FastSet;
import net.ushkinaz.storm8.domain.ClanInvite;
import net.ushkinaz.storm8.domain.ClanInviteStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Set;

@Singleton
public class CodesBlackList {
// ------------------------------ FIELDS ------------------------------

    private static final Logger LOGGER = LoggerFactory.getLogger(CodesBlackList.class);

    private Set<String> blackList = new FastSet<String>(5000);
    private ObjectContainer db;

// --------------------------- CONSTRUCTORS ---------------------------

    @Inject
    public CodesBlackList(ObjectContainer db) {
        this.db = db;
        initialize();
    }

    // -------------------------- OTHER METHODS --------------------------
    public boolean isBlackListed(String code) {
        return blackList.contains(code);
    }

    private void initialize() {
        LOGGER.debug(">> initialize");
        readFromFile();
        readFromDB();
        LOGGER.debug("Blacklist:" + blackList.size());
        LOGGER.debug("<< initialize");
    }

    private void readFromDB() {
        LOGGER.debug(">> readFromDB");
        Query query = db.query();
        query.constrain(ClanInvite.class);
        query.descend("status").constrain(ClanInviteStatus.NOT_FOUND);
        List<ClanInvite> codes = query.execute();
        for (ClanInvite clanInvite : codes) {
            blackList.add(clanInvite.getCode());
        }
        LOGGER.debug("<< readFromDB");
    }

    private void readFromFile() {
        LOGGER.debug(">> readFromFile");
        String newCode;
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader("black.list"));
            newCode = bufferedReader.readLine();
            do {
                blackList.add(newCode.trim().toUpperCase());
                newCode = bufferedReader.readLine();
            }
            while (newCode != null);
        } catch (FileNotFoundException e) {
            LOGGER.error("Error", e);
        } catch (IOException e) {
            LOGGER.error("Error", e);
        } finally {
            try {
                assert bufferedReader != null;
                bufferedReader.close();
            } catch (IOException e) {
                LOGGER.error("Error", e);
            }
        }
        LOGGER.debug("<< readFromFile");
    }
}
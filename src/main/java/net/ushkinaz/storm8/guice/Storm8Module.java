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

package net.ushkinaz.storm8.guice;

/*
* Created by IntelliJ IDEA.
* User: Dmitry Sidorenko
* Date: 23.05.2010
* Time: 23:30:39
*/

import com.db4o.ObjectContainer;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import javolution.xml.XMLBinding;
import net.ushkinaz.storm8.configuration.StormConfigurator;
import net.ushkinaz.storm8.dao.DB4OProvider;
import net.ushkinaz.storm8.digger.CodesDigger;
import net.ushkinaz.storm8.digger.LiveCodesDigger;
import net.ushkinaz.storm8.digger.annotations.*;
import net.ushkinaz.storm8.digger.forum.ForumCodesDigger;
import net.ushkinaz.storm8.domain.Configuration;
import net.ushkinaz.storm8.domain.Player;
import net.ushkinaz.storm8.domain.xml.XMLBinderFactory;
import net.ushkinaz.storm8.explorer.*;
import net.ushkinaz.storm8.invite.InviteService;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public class Storm8Module extends AbstractModule {
// ------------------------------ FIELDS ------------------------------

    @SuppressWarnings({"UnusedDeclaration"})
    private static final Logger LOGGER = getLogger(InviteService.class);
    protected String dbFile;

    private DB4OProvider db4oOProvider;
    private XMLBinderFactory xmlBinderFactory;
    private PlayerProvider playerProvider;

// --------------------------- CONSTRUCTORS ---------------------------

    public Storm8Module(String dbFile) {
        this.dbFile = dbFile;
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public DB4OProvider getDb4oOProvider() {
        return db4oOProvider;
    }

// -------------------------- OTHER METHODS --------------------------

    protected void configure() {
        bind(CodesDigger.class).to(ForumCodesDigger.class);

        xmlBinderFactory = new XMLBinderFactory();
        bind(XMLBinding.class).toProvider(xmlBinderFactory);

        db4oOProvider = createDB4OProvider();
        bind(ObjectContainer.class).toProvider(db4oOProvider);

        bind(DB4OProvider.class).toInstance(db4oOProvider);

        playerProvider = new PlayerProvider();
        bind(Player.class).toProvider(playerProvider);

        bind(PlayerProvider.class).toInstance(playerProvider);

        bind(Configuration.class).toProvider(StormConfigurator.class);

        bind(CodesDigger.class).annotatedWith(OfficialForum.class).to(ForumCodesDigger.class);
        bind(CodesDigger.class).annotatedWith(GetCodesLive.class).to(LiveCodesDigger.class);

        bind(VictimsScanner.class).annotatedWith(Clan.class).to(ClanScanner.class);
        bind(VictimsScanner.class).annotatedWith(HitList.class).to(HitListScanner.class);
        bind(VictimsScanner.class).annotatedWith(FightList.class).to(FightScanner.class);
        bind(VictimsScanner.class).annotatedWith(Comments.class).to(MyCommentsScanner.class);

        bind(VictimScanFilter.class).annotatedWith(ByName.class).to(VictimScanFilterByName.class);

        bind(String.class).annotatedWith(Names.named("victim name")).toInstance("Hong kong fue");

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                shutdown();
            }
        });
    }

    protected DB4OProvider createDB4OProvider() {
        return new DB4OProvider(dbFile);
    }

    public void shutdown() {
        db4oOProvider.shutdown();
    }
}

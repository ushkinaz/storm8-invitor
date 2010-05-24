package net.ushkinaz.storm8;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import net.ushkinaz.storm8.dao.DBConnector;
import net.ushkinaz.storm8.domain.Game;
import net.ushkinaz.storm8.forum.CodesDigger;
import net.ushkinaz.storm8.invite.InviteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


public class StormMe {
    @SuppressWarnings({"UnusedDeclaration"})
    private static final Logger LOGGER = LoggerFactory.getLogger(StormMe.class);

    private StormConfigurator configurator;
    private InviteService service;
    private CodesDigger digger;
    private DBConnector dbConnector;

    @Inject
    private void StormMe(StormConfigurator configurator, InviteService service, CodesDigger digger, DBConnector dbConnector) {
        this.configurator = configurator;
        this.service = service;
        this.digger = digger;
        this.dbConnector = dbConnector; 
    }

    public static void main(String[] args) throws Exception {
        Injector injector = Guice.createInjector(new Storm8Module());

        StormMe stormMe = injector.getInstance(StormMe.class);

        stormMe.doIt();

    }

    private void doIt() throws IOException {
        Game ninja = configurator.getGame("Ninja");

//        digger.digCodes(ninja.getGameCode());

        //TODO: read codes from file
//
//        codes = new HashSet<String>();
//        codesReader.readFromFile(CODES_FILENAME, codes);


        service.inviteClans(ninja);

        dbConnector.shutdown();
    }

}
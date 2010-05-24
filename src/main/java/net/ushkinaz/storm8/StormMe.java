package net.ushkinaz.storm8;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class StormMe {
    @SuppressWarnings({"UnusedDeclaration"})
    private static final Logger LOGGER = LoggerFactory.getLogger(StormMe.class);

    public static void main(String[] args) throws Exception {

        Injector injector = Guice.createInjector(new Storm8Module());

        StormConfigurator configurator = injector.getInstance(StormConfigurator.class);

//        InviteService instance = injector.getInstance(InviteService.class);
//
//        instance.inviteClans();
//
//        instance.shutdown();
    }

}
package net.ushkinaz.storm8;

import com.db4o.ObjectContainer;
import com.google.inject.Guice;
import com.google.inject.Injector;
import net.ushkinaz.storm8.domain.ClanInvite;
import net.ushkinaz.storm8.domain.Game;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;

/**
 * Convertor from HSQL to db4obj
 *
 * @author Dmitry Sidorenko
 */
public class DB4OPlay {
    private static final org.apache.commons.logging.Log LOGGER = org.apache.commons.logging.LogFactory.getLog(DB4OPlay.class);

    public static void main(String[] args) throws IOException, SQLException {
        Injector injector = Guice.createInjector(new Storm8Module("storm8.db"));
        ObjectContainer db = injector.getInstance(ObjectContainer.class);


        db.ext().backup("storm8.bak");

        StormConfigurator configurator = injector.getInstance(StormConfigurator.class);

        Game ninjaGame = configurator.getGame("ninja");

        ClanInvite inviteExample = new ClanInvite();

        Collection<ClanInvite> set = db.queryByExample(inviteExample);
        set.hashCode();
    }
}

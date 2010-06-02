package net.ushkinaz.storm8;

import com.db4o.ObjectContainer;
import com.google.inject.Guice;
import com.google.inject.Injector;
import net.ushkinaz.storm8.domain.ClanInvite;
import net.ushkinaz.storm8.domain.ClanInviteStatus;
import net.ushkinaz.storm8.guice.Storm8Module;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;

/**
 * Convertor from HSQL to db4obj
 *
 * @author Dmitry Sidorenko
 */
public class DB4OPlay {
// ------------------------------ FIELDS ------------------------------

    @SuppressWarnings({"UnusedDeclaration"})
    private static final Logger LOGGER = LoggerFactory.getLogger(DB4OPlay.class);

// --------------------------- main() method ---------------------------

    public static void main(String[] args) throws IOException, SQLException {
        Injector injector = Guice.createInjector(new Storm8Module("storm8.db"));
        ObjectContainer db = injector.getInstance(ObjectContainer.class);


        db.ext().backup("storm8.bak");

        ClanInvite invite = new ClanInvite();
        invite.setStatus(ClanInviteStatus.NOT_FOUND);
        Collection<ClanInvite> set = db.queryByExample(invite);
        for (ClanInvite clanInvite : set) {
            LOGGER.debug(clanInvite.getCode());
        }
        db.close();
    }
}

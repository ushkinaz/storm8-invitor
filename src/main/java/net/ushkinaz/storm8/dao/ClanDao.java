package net.ushkinaz.storm8.dao;

import com.db4o.ObjectContainer;
import com.db4o.query.Query;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.ushkinaz.storm8.domain.ClanInvite;
import net.ushkinaz.storm8.domain.ClanInviteStatus;
import net.ushkinaz.storm8.domain.Game;
import org.slf4j.Logger;

import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

@Singleton
public class ClanDao {
// ------------------------------ FIELDS ------------------------------

    private static final Logger LOGGER = getLogger(ClanDao.class);

    private ObjectContainer db;

// --------------------------- CONSTRUCTORS ---------------------------

    public ClanDao() {
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    @Inject
    public void setDb(ObjectContainer db) {
        this.db = db;
    }

// -------------------------- OTHER METHODS --------------------------

    public Collection<ClanInvite> getByStatus(Game game, ClanInviteStatus status) {
        ClanInvite clanInvite = new ClanInvite(game);
        clanInvite.setStatus(status);
        Query query = db.query();
        query.constrain(ClanInvite.class);
        query.descend("status").constrain(status);

        //ByExample does not work for some reason
        //db.queryByExample(clanInvite);
        @SuppressWarnings({"UnnecessaryLocalVariable"})
        List<ClanInvite> clanInvites = query.execute();
        return clanInvites;
    }

    public void updateClanInvite(ClanInvite clanInvite) {
        assert clanInvite != null;

        clanInvite.setDateUpdated(Calendar.getInstance().getTime());
        db.store(clanInvite);
        db.commit();
    }
}
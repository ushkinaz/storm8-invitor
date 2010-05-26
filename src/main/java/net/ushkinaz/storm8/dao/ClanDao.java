package net.ushkinaz.storm8.dao;

import com.db4o.ObjectContainer;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.ushkinaz.storm8.domain.ClanInvite;
import net.ushkinaz.storm8.domain.ClanInviteStatus;
import net.ushkinaz.storm8.domain.Game;
import org.slf4j.Logger;

import java.util.Calendar;
import java.util.Collection;

import static org.slf4j.LoggerFactory.getLogger;

@Singleton
public class ClanDao {
    private static final Logger LOGGER = getLogger(ClanDao.class);

    private ObjectContainer db;

    @Inject
    public ClanDao(ObjectContainer db) {
        this.db = db;
    }


    public void insertNewClanInvite(ClanInvite clanInvite) {
        if (db.ext().isStored(clanInvite)) {
            db.store(clanInvite);
            db.commit();
        }
    }

    public void updateClanDB(ClanInvite clanInvite) {
        assert clanInvite != null;

        clanInvite.setDateUpdated(Calendar.getInstance().getTime());
        db.store(clanInvite);
        db.commit();
    }

    public Collection<ClanInvite> getByStatus(Game game, ClanInviteStatus status) {
        ClanInvite clanInvite = new ClanInvite(game);
        clanInvite.setStatus(status);
        return db.queryByExample(clanInvite);
    }
}
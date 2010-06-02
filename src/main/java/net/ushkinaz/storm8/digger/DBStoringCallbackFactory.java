package net.ushkinaz.storm8.digger;

import com.db4o.ObjectContainer;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.ushkinaz.storm8.domain.ClanInvite;
import net.ushkinaz.storm8.domain.ClanInviteSource;
import net.ushkinaz.storm8.domain.ClanInviteStatus;
import net.ushkinaz.storm8.domain.Game;

/**
 * @author Dmitry Sidorenko
 */
@Singleton
public class DBStoringCallbackFactory {
// ------------------------------ FIELDS ------------------------------

    private ObjectContainer db;

// --------------------------- CONSTRUCTORS ---------------------------

    public DBStoringCallbackFactory() {
    }

    @Inject
    public void setDb(ObjectContainer db) {
        this.db = db;
    }

    public PageDigger.CodesDiggerCallback get(final Game game, final ClanInviteSource inviteSource) {
        return new PageDigger.CodesDiggerCallback() {
            @Override
            public void codeFound(String code) {
                ClanInvite clanInvite = new ClanInvite(code, game);
                if (db.queryByExample(clanInvite).size() == 0) {
                    clanInvite.setStatus(ClanInviteStatus.DIGGED);
                    clanInvite.setInviteSource(inviteSource);
                    db.store(clanInvite);
                    db.commit();
                }
            }
        };
    }

}

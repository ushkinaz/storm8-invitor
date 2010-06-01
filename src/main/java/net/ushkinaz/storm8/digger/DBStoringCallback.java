package net.ushkinaz.storm8.digger;

import com.db4o.ObjectContainer;
import net.ushkinaz.storm8.domain.ClanInvite;
import net.ushkinaz.storm8.domain.ClanInviteSource;
import net.ushkinaz.storm8.domain.ClanInviteStatus;
import net.ushkinaz.storm8.domain.Game;

/**
* @author Dmitry Sidorenko
* @date Jun 1, 2010
*/
public class DBStoringCallback implements PageDigger.CodesDiggerCallback {
    private Game game;
    private ClanInviteSource inviteSource;
    private ObjectContainer db;

    public DBStoringCallback(Game game, ClanInviteSource inviteSource, ObjectContainer db) {
        this.game = game;
        this.inviteSource = inviteSource;
        this.db = db;
    }

    public void codeFound(String code) {
        ClanInvite clanInvite = new ClanInvite(code, game);
        if (db.queryByExample(clanInvite).size() == 0) {
            clanInvite.setStatus(ClanInviteStatus.DIGGED);
            clanInvite.setInviteSource(inviteSource);
            db.store(clanInvite);
            db.commit();
        }
    }
}

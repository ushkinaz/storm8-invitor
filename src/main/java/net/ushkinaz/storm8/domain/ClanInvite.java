package net.ushkinaz.storm8.domain;

import com.db4o.config.annotations.Indexed;

import java.util.Date;

/**
 * Date: 25.05.2010
 * Created by Dmitry Sidorenko.
 */
public class ClanInvite {
    private static final long serialVersionUID = 3140559993012725638L;

    private String code;
    private Date dateRequested;
    private Date dateUpdated;
    @Indexed
    private Game game;
    private String name;
    @Indexed
    private ClanInviteStatus status;
    private ClanInviteSource inviteSource;


    public ClanInvite() {
    }

    public ClanInvite(Game game) {
        this.game = game;
    }

    /**
     * We've just found this clan code somewhere. Sore it.
     *
     * @param code code
     * @param game game. Really.
     */
    public ClanInvite(String code, Game game) {
        this.code = code;
        this.game = game;
    }

    public ClanInviteSource getInviteSource() {
        return inviteSource;
    }

    public void setInviteSource(ClanInviteSource inviteSource) {
        this.inviteSource = inviteSource;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getDateRequested() {
        return dateRequested;
    }

    public void setDateRequested(Date dateRequested) {
        this.dateRequested = dateRequested;
    }

    public Date getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(Date dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ClanInviteStatus getStatus() {
        return status;
    }

    public void setStatus(ClanInviteStatus status) {
        this.status = status;
    }


    @Override
    public String toString() {
        return "ClanInvite{" +
                "code='" + code + '\'' +
                ", game='" + game + '\'' +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", dateRequested=" + dateRequested +
                ", dateUpdated=" + dateUpdated +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClanInvite that = (ClanInvite) o;

        if (!code.equals(that.code)) return false;
        if (!game.equals(that.game)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = code.hashCode();
        result = 31 * result + game.hashCode();
        return result;
    }

    public boolean isInvited() {
        return !status.equals(ClanInviteStatus.DIGGED) ;
    }
}

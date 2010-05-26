package net.ushkinaz.storm8.domain;

import java.util.Date;

/**
 * Date: 25.05.2010
 * Created by Dmitry Sidorenko.
 */
public class ClanInvite {
    private String code;
    private Date dateRequested;
    private Date dateUpdated;
    private Game game;
    private String name;
    private ClanInviteStatus status;


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
                ", dateRequested=" + dateRequested +
                ", dateUpdated=" + dateUpdated +
                ", game='" + game + '\'' +
                ", name='" + name + '\'' +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClanInvite)) return false;

        ClanInvite that = (ClanInvite) o;

        if (code != null ? !code.equals(that.code) : that.code != null) return false;
        if (dateRequested != null ? !dateRequested.equals(that.dateRequested) : that.dateRequested != null)
            return false;
        if (dateUpdated != null ? !dateUpdated.equals(that.dateUpdated) : that.dateUpdated != null) return false;
        if (game != null ? !game.equals(that.game) : that.game != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (status != that.status) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = code != null ? code.hashCode() : 0;
        result = 31 * result + (dateRequested != null ? dateRequested.hashCode() : 0);
        result = 31 * result + (dateUpdated != null ? dateUpdated.hashCode() : 0);
        result = 31 * result + (game != null ? game.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        return result;
    }

    public boolean isInvited() {
        return status != null;
    }
}

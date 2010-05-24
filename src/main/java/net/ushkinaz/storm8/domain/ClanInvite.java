package net.ushkinaz.storm8.domain;

import java.sql.Date;

/**
 * Date: 25.05.2010
 * Created by Dmitry Sidorenko.
 */
public class ClanInvite {
    private String code;
    private Date dateRequested;
    private Date dateUpdated;
    private String game;
    private String name;
    private ClanInviteStatus status;


    public ClanInvite() {
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

    public String getGame() {
        return game;
    }

    public void setGame(String game) {
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
}

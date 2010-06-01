package net.ushkinaz.storm8.domain;

/**
 * Date: 23.05.2010
 * Created by Dmitry Sidorenko.
 */
public enum ClanInviteStatus {
    DIGGED(0),
    REQUESTED(1),
    PENDING(2),
    ACCEPTED(4),
    NOT_FOUND(8);

    private int status;

    ClanInviteStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}

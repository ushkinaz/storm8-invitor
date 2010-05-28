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

    public static ClanInviteStatus getByStatus(int status) {
        ClanInviteStatus clanInviteStatus = null;
        switch (status) {
            case 1:
                clanInviteStatus = REQUESTED;
                break;
            case 2:
                clanInviteStatus = PENDING;
                break;
            case 4:
                clanInviteStatus = ACCEPTED;
                break;
            case 8:
                clanInviteStatus = NOT_FOUND;
                break;
        }
        assert clanInviteStatus != null;
        assert clanInviteStatus.getStatus() == status;

        return clanInviteStatus;
    }
}

package net.ushkinaz.storm8.invite;

import net.ushkinaz.storm8.domain.ClanInvite;
import net.ushkinaz.storm8.http.PostBodyFactory;
import org.apache.commons.httpclient.NameValuePair;

/**
 * @author Dmitry Sidorenko
 * @date May 25, 2010
 */
public class InviteClanPostBodyFactory implements PostBodyFactory {
    private static final org.apache.commons.logging.Log LOGGER = org.apache.commons.logging.LogFactory.getLog(InviteClanPostBodyFactory.class);

    private static final String FORM_ACTION = "action";
    private static final String FORM_MOBCODE = "mobcode";

    private ClanInvite clanInvite;

    public InviteClanPostBodyFactory(ClanInvite clanInvite) {
        this.clanInvite = clanInvite;
    }

    public NameValuePair[] createBody() {
        return new NameValuePair[]{
                new NameValuePair(FORM_ACTION, "Invite"),
                new NameValuePair(FORM_MOBCODE, clanInvite.getCode())
        };
    }
}

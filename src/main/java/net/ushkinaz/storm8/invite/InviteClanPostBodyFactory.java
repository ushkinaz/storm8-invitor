package net.ushkinaz.storm8.invite;

import net.ushkinaz.storm8.domain.ClanInvite;
import net.ushkinaz.storm8.http.PostBodyFactory;
import org.apache.commons.httpclient.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Dmitry Sidorenko
 * @date May 25, 2010
 */
public class InviteClanPostBodyFactory implements PostBodyFactory {
// ------------------------------ FIELDS ------------------------------

    @SuppressWarnings({"UnusedDeclaration"})
    private static final Logger LOGGER = LoggerFactory.getLogger(InviteClanPostBodyFactory.class);

    private static final String FORM_ACTION = "action";
    private static final String FORM_MOBCODE = "mobcode";

    private ClanInvite clanInvite;

// --------------------------- CONSTRUCTORS ---------------------------

    public InviteClanPostBodyFactory(ClanInvite clanInvite) {
        this.clanInvite = clanInvite;
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface PostBodyFactory ---------------------

    public NameValuePair[] createBody() {
        return new NameValuePair[]{
                new NameValuePair(FORM_ACTION, "Invite"),
                new NameValuePair(FORM_MOBCODE, clanInvite.getCode())
        };
    }
}

package net.ushkinaz.storm8;



/*
 * Created by IntelliJ IDEA.
 * User: Dmitry Sidorenko
 * Date: 23.05.2010
 * Time: 23:30:39
 */

import com.google.inject.AbstractModule;
import javolution.xml.XMLBinding;
import net.ushkinaz.storm8.domain.xml.XMLBinderFactory;
import net.ushkinaz.storm8.forum.CodesDigger;
import net.ushkinaz.storm8.forum.ForumCodesDigger;
import net.ushkinaz.storm8.invite.InviteService;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public class Storm8Module extends AbstractModule {
    private static final Logger LOGGER = getLogger(InviteService.class);

    protected void configure() {
        bind(CodesDigger.class).to(ForumCodesDigger.class);

        bind(XMLBinding.class).toProvider(new XMLBinderFactory());
    }
}

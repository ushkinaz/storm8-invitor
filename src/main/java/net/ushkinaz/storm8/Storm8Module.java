package net.ushkinaz.storm8;



/*
 * Created by IntelliJ IDEA.
 * User: Dmitry Sidorenko
 * Date: 23.05.2010
 * Time: 23:30:39
 */

import com.db4o.ObjectContainer;
import com.google.inject.AbstractModule;
import javolution.xml.XMLBinding;
import net.ushkinaz.storm8.dao.DB4OProvider;
import net.ushkinaz.storm8.dao.DBConnector;
import net.ushkinaz.storm8.domain.xml.XMLBinderFactory;
import net.ushkinaz.storm8.forum.CodesDigger;
import net.ushkinaz.storm8.forum.ForumCodesDigger;
import net.ushkinaz.storm8.invite.InviteService;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;

import static org.slf4j.LoggerFactory.getLogger;

public class Storm8Module extends AbstractModule {
    private static final Logger LOGGER = getLogger(InviteService.class);
    private DB4OProvider db4oOProvider;
    private XMLBinderFactory xmlBinderFactory;
    protected String dbFile;


    public Storm8Module(String dbFile) {
        this.dbFile = dbFile;
    }

    protected void configure() {
        bind(CodesDigger.class).to(ForumCodesDigger.class);

        xmlBinderFactory = new XMLBinderFactory();
        bind(XMLBinding.class).toProvider(xmlBinderFactory);

        db4oOProvider = createDB4OProvider();
        bind(ObjectContainer.class).toProvider(db4oOProvider);

        final DBConnector connector;
        try {
            connector = new DBConnector();
            bind(Connection.class).toProvider(connector);
        } catch (IOException e) {
            LOGGER.error("DB connection error", e);
            throw new IllegalStateException(e);
        }
    }

    protected DB4OProvider createDB4OProvider() {
        new File(dbFile).delete();
        return new DB4OProvider(dbFile);
    }

    public DB4OProvider getDb4oOProvider() {
        return db4oOProvider;
    }

    public XMLBinderFactory getXmlBinderFactory() {
        return xmlBinderFactory;
    }
}

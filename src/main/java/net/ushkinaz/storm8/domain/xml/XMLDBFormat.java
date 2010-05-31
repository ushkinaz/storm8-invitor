package net.ushkinaz.storm8.domain.xml;

import com.db4o.ObjectContainer;
import com.db4o.query.Query;
import javolution.xml.XMLFormat;
import javolution.xml.stream.XMLStreamException;
import net.ushkinaz.storm8.domain.Identifiable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * A workaround.
 * Date: 27.05.2010
 * Created by Dmitry Sidorenko.
 */
public abstract class XMLDBFormat<T extends Identifiable> extends XMLFormat<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(XMLDBFormat.class);

    private static final String ID_ATTRIBUTE = "id";

    protected static ObjectContainer db;

    protected XMLDBFormat(Class<T> forClass) {
        super(forClass);
    }

    /**
     * If object with given ID attribute is already exist in DB, then return that object instead of creating new one.
     */
    @Override
    public T newInstance(Class<T> cls, XMLFormat.InputElement xml) throws XMLStreamException {
        if (db == null) {
            LOGGER.warn("No database is set. Operating in detached mode. Objects read from XML will not be updated in DB");
            return super.newInstance(cls, xml);
        }

        final String id = xml.getAttribute(ID_ATTRIBUTE, "");

        Query query = db.query();
        query.constrain(cls);
        query.descend(ID_ATTRIBUTE).constrain(id);
        List<T> gamesDB = query.execute();
        if (gamesDB.size() > 0) {
            assert gamesDB.size() == 1;
            return gamesDB.get(0);
        } else {
            return super.newInstance(cls, xml);
        }
    }

    @Override
    public void write(T obj, OutputElement xml) throws XMLStreamException {
        xml.setAttribute(ID_ATTRIBUTE, obj.getId());
    }

    @Override
    public void read(InputElement xml, T obj) throws XMLStreamException {
        obj.setId(xml.getAttribute(ID_ATTRIBUTE, ""));
    }

    public static void setDb(ObjectContainer db) {
        XMLDBFormat.db = db;
    }
}

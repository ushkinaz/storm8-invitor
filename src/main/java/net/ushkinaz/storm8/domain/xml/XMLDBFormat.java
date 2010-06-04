/*
 * Copyright (c) 2010-2010, Dmitry Sidorenko. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
// ------------------------------ FIELDS ------------------------------

    private static final Logger LOGGER = LoggerFactory.getLogger(XMLDBFormat.class);

    protected static final String ID_ATTRIBUTE = "id";
    protected static final String REF_ID_ATTRIBUTE = "ref-id";

    protected static ObjectContainer db;

// -------------------------- STATIC METHODS --------------------------

    public static void setDb(ObjectContainer db) {
        XMLDBFormat.db = db;
    }

// --------------------------- CONSTRUCTORS ---------------------------

    protected XMLDBFormat(Class<T> forClass) {
        super(forClass);
    }

// -------------------------- OTHER METHODS --------------------------

    /**
     * If object with given ID attribute is already exist in DB, then return that object instead of creating new one.
     */
    @Override
    public T newInstance(Class<T> cls, XMLFormat.InputElement xml) throws XMLStreamException {
        if (db == null) {
            LOGGER.warn("No database is set. Operating in detached mode. Objects read from XML will not be updated in DB");
            return super.newInstance(cls, xml);
        }

        String id = null;
        if (xml.getAttribute(REF_ID_ATTRIBUTE, null) != null) {
            id = xml.getAttribute(REF_ID_ATTRIBUTE, "");
        } else if (xml.getAttribute(ID_ATTRIBUTE, null) != null) {
            id = xml.getAttribute(ID_ATTRIBUTE, "");
        } else {
            throw new XMLStreamException(cls.getName() + " xml should have an id attribute.");
        }

        Query query = db.query();
        query.constrain(cls);
        query.descend(ID_ATTRIBUTE).constrain(id);
        List<T> gamesDB = query.execute();
        if (gamesDB.size() > 0) {
            assert gamesDB.size() == 1;
            return gamesDB.get(0);
        } else {
            T entity = super.newInstance(cls, xml);
            entity.setId(id);
            db.store(entity);
            return entity;
        }
    }

    @Override
    public void read(InputElement xml, T obj) throws XMLStreamException {
        //id is read directly in instance creation.
        //obj.setId(xml.getAttribute(ID_ATTRIBUTE, ""));
    }

    @Override
    public void write(T obj, OutputElement xml) throws XMLStreamException {
        xml.setAttribute(ID_ATTRIBUTE, obj.getId());
    }
}

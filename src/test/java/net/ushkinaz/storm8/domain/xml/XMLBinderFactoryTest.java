package net.ushkinaz.storm8.domain.xml;
/**
 * @author Dmitry Sidorenko
 * @date May 24, 2010
 */

import org.junit.Assert;
import org.junit.Test;

public class XMLBinderFactoryTest {
    private XMLBinderFactory xmlBinderFactory = new XMLBinderFactory();

    @Test
    public void testGetXMLBinding() throws Exception {
        Assert.assertNotNull(xmlBinderFactory.get());
    }
}
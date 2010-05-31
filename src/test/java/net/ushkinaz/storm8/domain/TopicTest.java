package net.ushkinaz.storm8.domain;
/**
 * @author Dmitry Sidorenko
 * @date May 24, 2010
 */

import javolution.xml.XMLBinding;
import javolution.xml.XMLObjectReader;
import javolution.xml.XMLObjectWriter;
import javolution.xml.stream.XMLStreamException;
import net.ushkinaz.storm8.domain.xml.XMLBinderFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class TopicTest {
    private Topic topic;
    private static final String TOPICS_XML = "topics-test.xml";
    private XMLBinding binding;


    @Before
    public void beforeClass(){
        XMLBinderFactory xmlBinderFactory = new XMLBinderFactory();
        binding = xmlBinderFactory.get();

        topic = new Topic();
        topic.setTopicId(11138);
        topic.setPages(100);
        topic.setLastProcessedPage(0);
        topic.setProcessedDate(null);
    }

    @Test
    public void testSerialization() throws XMLStreamException, FileNotFoundException {

        // Writes the area to a file.
        XMLObjectWriter writer = XMLObjectWriter.newInstance(new FileOutputStream(TOPICS_XML, false));
        writer.setBinding(binding); // Optional.
        writer.setIndentation("\t"); // Optional (use tabulation for indentation).
        writer.write(topic, "Topic", Topic.class);
        writer.close();

        // Reads the area back
        XMLObjectReader reader = XMLObjectReader.newInstance(new FileInputStream(TOPICS_XML));
        reader.setBinding(binding);
        Topic xmlTopic = reader.read("Topic", Topic.class);
        reader.close();

        Assert.assertEquals(topic, xmlTopic);
    }
}
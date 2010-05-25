package net.ushkinaz.storm8.domain;

import javolution.xml.XMLFormat;
import javolution.xml.XMLSerializable;
import javolution.xml.stream.XMLStreamException;

import java.sql.Date;

/**
 * @author Dmitry Sidorenko
 */
public class Topic implements XMLSerializable {
    private static final org.apache.commons.logging.Log LOGGER = org.apache.commons.logging.LogFactory.getLog(Topic.class);


    private int topicId;
    private int pages;
    private int lastProcessedPage;
    private Date dateUpdated;

    public Topic() {
    }


    public Topic(int topicId) {
        this.topicId = topicId;
    }

    public int getTopicId() {
        return topicId;
    }

    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public int getLastProcessedPage() {
        return lastProcessedPage;
    }

    public void setLastProcessedPage(int lastProcessedPage) {
        this.lastProcessedPage = lastProcessedPage;
    }

    public Date getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(Date dateUpdated) {
        this.dateUpdated = dateUpdated;
    }


    @SuppressWarnings({"UnusedDeclaration"})
    protected static final XMLFormat<Topic> TOPIC_XML = new XMLFormat<Topic>(Topic.class) {
        public void write(Topic topic, OutputElement xml) throws XMLStreamException {
            xml.setAttribute("topicId", topic.topicId);
        }

        public void read(InputElement xml, Topic topic) throws XMLStreamException {
            topic.topicId = xml.getAttribute("topicId", 0);
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Topic)) return false;

        Topic topic = (Topic) o;

        if (topicId != topic.topicId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return topicId;
    }
}
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

package net.ushkinaz.storm8.domain;

import com.db4o.config.annotations.Indexed;
import javolution.xml.XMLFormat;
import javolution.xml.XMLSerializable;
import javolution.xml.stream.XMLStreamException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * @author Dmitry Sidorenko
 */
public class Topic implements XMLSerializable {
// ------------------------------ FIELDS ------------------------------

    @SuppressWarnings({"UnusedDeclaration"})
    private static final Logger LOGGER = LoggerFactory.getLogger(Topic.class);

    private static final long serialVersionUID = -4998697492471291093L;

    @SuppressWarnings({"UnusedDeclaration"})
    protected static final XMLFormat<Topic> TOPIC_XML = new XMLFormat<Topic>(Topic.class) {
        public void write(Topic topic, OutputElement xml) throws XMLStreamException {
            xml.setAttribute("topicId", topic.topicId);
        }

        public void read(InputElement xml, Topic topic) throws XMLStreamException {
            topic.topicId = xml.getAttribute("topicId", 0);
        }
    };


    @Indexed
    private int topicId;
    private int pages;
    private int lastProcessedPage;
    private Date processedDate;
    /**
     * Number of posts in the thread, as reported on forum page.
     */
    private int posts;
    /**
     * Indicates that new posts were added since last check
     */
    private transient boolean postsAdded = false;

// --------------------------- CONSTRUCTORS ---------------------------

    public Topic() {
    }

    public Topic(int topicId) {
        this.topicId = topicId;
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public int getLastProcessedPage() {
        return lastProcessedPage;
    }

    public void setLastProcessedPage(int lastProcessedPage) {
        this.lastProcessedPage = lastProcessedPage;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public int getPosts() {
        return posts;
    }

    public void setPosts(int posts) {
        this.posts = posts;
    }

    public Date getProcessedDate() {
        return processedDate;
    }

    public void setProcessedDate(Date processedDate) {
        this.processedDate = processedDate;
    }

    public int getTopicId() {
        return topicId;
    }

    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }

    public void setPostsAdded(boolean postsAdded) {
        this.postsAdded = postsAdded;
    }

// ------------------------ CANONICAL METHODS ------------------------

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

    @Override
    public String toString() {
        return "Topic{" +
                "topicId=" + topicId +
                ", pages=" + pages +
                ", posts=" + posts +
                ", lastProcessedPage=" + lastProcessedPage +
                ", processedDate=" + processedDate +
                ", postsAdded=" + postsAdded +
                '}';
    }

// -------------------------- OTHER METHODS --------------------------

    public boolean arePostsAdded() {
        return postsAdded;
    }
}

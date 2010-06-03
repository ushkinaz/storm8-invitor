package net.ushkinaz.storm8.http;

import org.apache.commons.httpclient.NameValuePair;

/**
 * @author Dmitry Sidorenko
 * @date May 25, 2010
 */
public interface PostBodyFactory {
// ------------------------------ FIELDS ------------------------------

    /**
     * Null object
     */
    public static final PostBodyFactory NULL = new PostBodyFactory() {
        @Override
        public NameValuePair[] createBody() {
            return new NameValuePair[0];
        }
    };

// -------------------------- OTHER METHODS --------------------------

    NameValuePair[] createBody();
}

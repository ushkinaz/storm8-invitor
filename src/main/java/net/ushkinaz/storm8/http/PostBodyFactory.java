package net.ushkinaz.storm8.http;

import org.apache.commons.httpclient.NameValuePair;

/**
 * @author Dmitry Sidorenko
 * @date May 25, 2010
 */
public interface PostBodyFactory {
    NameValuePair[] createBody();
}

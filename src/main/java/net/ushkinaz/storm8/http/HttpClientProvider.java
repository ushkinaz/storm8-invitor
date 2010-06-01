package net.ushkinaz.storm8.http;

import com.google.inject.Provider;
import org.apache.commons.httpclient.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Dmitry Sidorenko
 * @date May 28, 2010
 */
public class HttpClientProvider implements Provider<HttpClient> {
// ------------------------------ FIELDS ------------------------------

    @SuppressWarnings({"UnusedDeclaration"})
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientProvider.class);

    static final String HTTP_PROXY_HOST = "http.proxyHost";
    static final String HTTP_PROXY_PORT = "http.proxyPort";

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface Provider ---------------------

    @Override
    public HttpClient get() {
        HttpClient httpClient = new HttpClient();

        if (System.getProperty(HTTP_PROXY_HOST) != null) {
            httpClient.getHostConfiguration().setProxy(System.getProperty(HTTP_PROXY_HOST), Integer.getInteger(HTTP_PROXY_PORT, 3128));
        }
        return httpClient;
    }
}

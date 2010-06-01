package net.ushkinaz.storm8.http;

import com.google.inject.Inject;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Dmitry Sidorenko
 */
public class HttpService {
    @SuppressWarnings({"UnusedDeclaration"})
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpService.class);

    private HttpClientProvider clientProvider;
    private ThreadLocal<HttpClient> httpClientThreadLocal = new ThreadLocal<HttpClient>();

    protected HttpService(HttpClientProvider clientProvider) {
        this.clientProvider = clientProvider;
    }

    /**
     * Gets thread local instance of HttpClient.
     *
     * @return HttpClient
     */
    final protected HttpClient getClient() {
        if (httpClientThreadLocal.get() == null) {
            HttpClient httpClient = clientProvider.get();
            initHttpClient(httpClient);
            httpClientThreadLocal.set(httpClient);
        }
        return httpClientThreadLocal.get();
    }

    protected void initHttpClient(HttpClient httpClient) {
        httpClient.getParams().setCookiePolicy(CookiePolicy.RFC_2109);
        httpClient.getParams().setParameter(HttpMethodParams.USER_AGENT, "Mozilla/5.0 (Linux; U; Android 2.1; ru-ru; HTC Legend Build/ERD79) AppleWebKit/530.17 (KHTML, like Gecko) Version/4.0 Mobile Safari/530.17");
    }
}

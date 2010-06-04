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
public abstract class HttpService {
// ------------------------------ FIELDS ------------------------------

    @SuppressWarnings({"UnusedDeclaration"})
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpService.class);

    private HttpClientProvider clientProvider;
    private ThreadLocal<HttpClient> httpClientThreadLocal = new ThreadLocal<HttpClient>();

// --------------------------- CONSTRUCTORS ---------------------------

    protected HttpService() {
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    @Inject
    public void setClientProvider(HttpClientProvider clientProvider) {
        this.clientProvider = clientProvider;
    }

// -------------------------- OTHER METHODS --------------------------

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

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

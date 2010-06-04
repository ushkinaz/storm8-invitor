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

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpHelper {
// ------------------------------ FIELDS ------------------------------

    @SuppressWarnings({"UnusedDeclaration"})
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpHelper.class);

// -------------------------- STATIC METHODS --------------------------

    public static Matcher getHttpMatcher(HttpClient httpClient, HttpMethod httpMethod, Pattern pattern) {
        return pattern.matcher(getHttpResponse(httpClient, httpMethod));
    }

    public static String getHttpResponse(HttpClient httpClient, HttpMethod httpMethod) {
        String asString = "";
        try {
            int statusCode = httpClient.executeMethod(httpMethod);
            if (statusCode != 200) {
                throw new IOException("Can't access page : " + httpMethod.getURI());
            }
            asString = httpMethod.getResponseBodyAsString();
        } catch (IOException e) {
            LOGGER.error("Error", e);
        }
        return asString;
    }
}
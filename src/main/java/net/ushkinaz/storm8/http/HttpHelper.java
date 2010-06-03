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
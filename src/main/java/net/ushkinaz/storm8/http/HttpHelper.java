package net.ushkinaz.storm8.http;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpHelper {

    public static Matcher getHttpMatcher(HttpClient httpClient, HttpMethod httpMethod, Pattern pattern) throws IOException {
        String page = getHttpResponse(httpClient, httpMethod);
        return pattern.matcher(page);
    }

    public static String getHttpResponse(HttpClient httpClient, HttpMethod httpMethod) throws IOException {
        int statusCode = httpClient.executeMethod(httpMethod);
        if (statusCode != 200) {
            throw new IOException("Can't access page : " + httpMethod.getURI());
        }
        return httpMethod.getResponseBodyAsString();
    }
}
package net.ushkinaz.storm8.http;

/**
 * Thrown when game page has expired
 *
 * @author Dmitry Sidorenko
 */
public class PageExpiredException extends RuntimeException {
    private String url;

    public PageExpiredException(String url) {
        this.url = url;
    }

    public PageExpiredException() {
    }

    @Override
    public String toString() {
        return String.format("URL expired: %s", url);
    }
}

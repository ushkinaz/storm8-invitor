package net.ushkinaz.storm8.http;

/**
 * Thrown when game page has expired
 *
 * @author Dmitry Sidorenko
 */
public class PageExpiredException extends RuntimeException {
// ------------------------------ FIELDS ------------------------------

    private String url;

// --------------------------- CONSTRUCTORS ---------------------------

    public PageExpiredException() {
    }

    public PageExpiredException(String url) {
        this.url = url;
    }

// ------------------------ CANONICAL METHODS ------------------------

    @Override
    public String toString() {
        return String.format("URL expired: %s", url);
    }
}

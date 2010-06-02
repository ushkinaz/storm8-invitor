package net.ushkinaz.storm8.http;

/**
 * Thrown when game page has expired
 *
 * @author Dmitry Sidorenko
 */
public class PageExpiredException extends Exception {
    public PageExpiredException() {
    }

    public PageExpiredException(String message) {
        super(message);
    }

    public PageExpiredException(String message, Throwable cause) {
        super(message, cause);
    }

    public PageExpiredException(Throwable cause) {
        super(cause);
    }
}

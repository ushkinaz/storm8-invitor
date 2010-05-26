package net.ushkinaz.storm8.http;

/**
 * Game server did not recognize request.
 *
 * @author Dmitry Sidorenko
 */
public class ServerWorkflowException extends Exception {
    private static final org.apache.commons.logging.Log LOGGER = org.apache.commons.logging.LogFactory.getLog(ServerWorkflowException.class);

    public ServerWorkflowException() {
    }

    public ServerWorkflowException(String message) {
        super(message);
    }

    public ServerWorkflowException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServerWorkflowException(Throwable cause) {
        super(cause);
    }
}

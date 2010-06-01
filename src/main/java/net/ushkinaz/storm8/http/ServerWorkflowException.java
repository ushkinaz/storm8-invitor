package net.ushkinaz.storm8.http;

/**
 * Game server did not recognize request.
 *
 * @author Dmitry Sidorenko
 */
public class ServerWorkflowException extends Exception {
// --------------------------- CONSTRUCTORS ---------------------------

    public ServerWorkflowException() {
    }

    public ServerWorkflowException(String message) {
        super(message);
    }

    public ServerWorkflowException(Throwable cause) {
        super(cause);
    }

    public ServerWorkflowException(String message, Throwable cause) {
        super(message, cause);
    }
}

package de.theves.denon4j.net;

/**
 * Thrown if connection establishment/disconnect fails.
 *
 * @author Sascha Theves
 */
public class ConnectException extends ConnectionException {
    private static final long serialVersionUID = 1L;

    public ConnectException(Throwable t) {
        super(t);
    }

    public ConnectException(String message) {
        super(message);
    }

    public ConnectException(String message, Throwable e) {
        super(message, e);
    }
}

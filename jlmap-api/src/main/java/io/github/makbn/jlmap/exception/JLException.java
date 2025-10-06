package io.github.makbn.jlmap.exception;

/**
 * Internal JLMap application's Exception base class.
 *
 * @author Matt Akbarian  (@makbn)
 */
public class JLException extends RuntimeException {
    public JLException(String message) {
        super(message);
    }

    public JLException(Throwable cause) {
        super(cause);
    }

    public JLException(String message, Throwable cause) {
        super(message, cause);
    }
}

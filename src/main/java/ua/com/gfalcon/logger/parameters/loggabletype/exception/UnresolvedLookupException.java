package ua.com.gfalcon.logger.parameters.loggabletype.exception;

/**
 * Lookup exception unresolved.
 */
public class UnresolvedLookupException extends RuntimeException {
    public UnresolvedLookupException() {
    }

    public UnresolvedLookupException(String message) {
        super(message);
    }
}

package com.epam.esm.exception;

/**
 * Exception: Thrown if a database-insertion violates a UNIQUE-constraint.
 */
public class DuplicateException extends AppException {

    /**
     * Constructs a new runtime exception with {@code null} as its
     * detail message.  The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause}.
     *
     * @param errorCode is the code of an error.
     * @param errorMessage is the message of an error.
     */
    public DuplicateException(String errorCode, Object errorMessage) {
        super(errorCode, errorMessage);
    }
}

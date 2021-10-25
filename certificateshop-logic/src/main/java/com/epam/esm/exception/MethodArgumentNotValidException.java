package com.epam.esm.exception;

/**
 * Exception: Thrown if a method-argument is not valid.
 */
public class MethodArgumentNotValidException extends AppException {
    /**
     * Constructs a new MethodArgumentNotValidException.
     *
     * @param errorCode    is the code of an error.
     * @param errorMessage is the message of an error.
     */
    public MethodArgumentNotValidException(String errorCode, Object errorMessage) {
        super(errorCode, errorMessage);
    }
}

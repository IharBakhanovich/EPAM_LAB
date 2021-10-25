package com.epam.esm.exception;

/**
 * Exception: Thrown if an entity was not found.
 */
public class EntityNotFoundException extends AppException {

    /**
     * Constructs a new EntityNotFoundException.
     *
     * @param errorCode    is the code of an error.
     * @param errorMessage is the message of an error.
     */
    public EntityNotFoundException(String errorCode, Object errorMessage) {
        super(errorCode, errorMessage);
    }

}

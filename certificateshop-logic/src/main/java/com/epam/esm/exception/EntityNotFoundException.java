package com.epam.esm.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception: Thrown if an entity was not found.
 */
public class EntityNotFoundException extends AppException {

    /**
     * Constructs a new EntityNotFoundException.
     *
     * @param errorCode is the code of an error.
     * @param errorMessage is the message of an error.
     */
    public EntityNotFoundException(String errorCode, Object errorMessage) {
        super(errorCode, errorMessage);
    }

}

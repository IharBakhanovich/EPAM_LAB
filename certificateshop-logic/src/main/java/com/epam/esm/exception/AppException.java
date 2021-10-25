package com.epam.esm.exception;

/**
 * The main class of the application {@link RuntimeException}.
 */
public class AppException extends RuntimeException {
    String errorCode;
    Object errorMessage;

    /**
     * Constructs the {@link AppException}.
     *
     * @param errorCode    is the code of the error to set.
     * @param errorMessage is the messages {@link Object} to set.
     */
    AppException(String errorCode, Object errorMessage) {
        super();
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    /**
     * The 'errorCode' getter.
     *
     * @return the 'errorCode'.
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * The 'errorMessage' getter.
     *
     * @return the 'errorMessage'.
     */
    public Object getErrorMessage() {
        return errorMessage;
    }
}

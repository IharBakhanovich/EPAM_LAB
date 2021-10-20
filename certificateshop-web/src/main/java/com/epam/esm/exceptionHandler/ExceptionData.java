package com.epam.esm.exceptionHandler;

/**
 * The class to contain all the exception data.
 */
public class ExceptionData {
    private String errorCode;
    private Object errorMessage;

    /**
     * Constructs the {@link ExceptionData}.
     */
    public ExceptionData() {
    }

    /**
     * The 'errorCode' getter.
     *
     * @return 'errorCode'.
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Sets 'errorCode'.
     *
     * @param errorCode is the {@link String} to set.
     */
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * The 'errorMessage' getter.
     *
     * @return 'errorMessage'.
     */
    public Object getErrorMessage() {
        return errorMessage;
    }

    /**
     * Sets 'errorMessage'.
     *
     * @param errorMessage is the {@link Object} to set.
     */
    public void setErrorMessage(Object errorMessage) {
        this.errorMessage = errorMessage;
    }
}

package com.epam.esm.exception;

public class AppException extends RuntimeException {
    String errorCode;
    Object errorMessage;

    AppException(String errorCode, Object errorMessage) {
        super();
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public Object getErrorMessage() {
        return errorMessage;
    }
}

package com.epam.esm.exceptionHandler;

import com.epam.esm.exception.DuplicateException;
import com.epam.esm.exception.EntityNotFoundException;
import com.epam.esm.exception.MethodArgumentNotValidException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CertificateShopExceptionHandler {

    public static final String ERROR_CODE_FOR_OTHER_EXCEPTION = "40099";

    @ExceptionHandler
    public ResponseEntity<ExceptionData> handleEntityNotFoundException(EntityNotFoundException exception) {
        ExceptionData exceptionData = new ExceptionData();
        exceptionData.setErrorCode(exception.getErrorCode());
        exceptionData.setErrorMessage(exception.getErrorMessage());
        return new ResponseEntity<>(exceptionData, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionData> handleDuplicateException(DuplicateException exception) {
        ExceptionData exceptionData = new ExceptionData();
        exceptionData.setErrorCode(exception.getErrorCode());
        exceptionData.setErrorMessage(exception.getErrorMessage());
        return new ResponseEntity<>(exceptionData, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionData> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        ExceptionData exceptionData = new ExceptionData();
        exceptionData.setErrorCode(exception.getErrorCode());
        exceptionData.setErrorMessage(exception.getErrorMessage());
        return new ResponseEntity<>(exceptionData, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionData> handleOtherException(Exception exception) {
        ExceptionData exceptionData = new ExceptionData();
        exceptionData.setErrorCode(ERROR_CODE_FOR_OTHER_EXCEPTION);
        exceptionData.setErrorMessage(exception.getMessage());
        return new ResponseEntity<>(exceptionData, HttpStatus.BAD_REQUEST);
    }
}

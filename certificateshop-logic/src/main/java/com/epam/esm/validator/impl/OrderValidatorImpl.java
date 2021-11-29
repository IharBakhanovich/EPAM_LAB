package com.epam.esm.validator.impl;

import com.epam.esm.configuration.Translator;
import com.epam.esm.exception.MethodArgumentNotValidException;
import com.epam.esm.model.impl.GiftCertificate;
import com.epam.esm.model.impl.Order;
import com.epam.esm.validator.CertificateValidator;
import com.epam.esm.validator.OrderValidator;
import com.epam.esm.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class OrderValidatorImpl implements OrderValidator {
    public static final String ERROR_CODE_METHOD_ARGUMENT_NOT_VALID = "400";
    public static final String ERROR_CODE_ORDER_NOT_VALID = "03";

    private Translator translator;
    private CertificateValidator certificateValidator;
    private UserValidator userValidator;

    @Autowired
    public OrderValidatorImpl(Translator translator,
                              CertificateValidator certificateValidator, UserValidator userValidator) {
        this.translator = translator;
        this.certificateValidator = certificateValidator;
        this.userValidator = userValidator;
    }

    private static boolean isUTF8(final byte[] inputBytes) {
        final String converted = new String(inputBytes, StandardCharsets.UTF_8);
        final byte[] outputBytes = converted.getBytes(StandardCharsets.UTF_8);
        return Arrays.equals(inputBytes, outputBytes);
    }

    /**
     * Validates {@link Order}.
     *
     * @param order                   is {@link Order} to validate.
     * @param isEmptyFieldsAreChecked is the boolean to show whether the empty fields must be validated.
     */
    @Override
    public void validateOrder(Order order, boolean isEmptyFieldsAreChecked) {
        List<String> errorMessage = new ArrayList<>();
        if (isEmptyFieldsAreChecked) {
            checkEmptyFields(order, errorMessage);
        }

        if (!isNameValid(order.getName(), 60)) {
            errorMessage.add(translator.toLocale(
                    "ORDER_NAME_SHOULD_CONTAIN_ONLY_LATIN_LETTERS_AND_SHOULD_BE_NOT_MORE_THAN_30_SIGNS_LONG"));
        }

        userValidator.validateUser(order.getUser(), true);
        for (GiftCertificate certificate : order.getCertificates()) {
            certificateValidator.validateCertificate(certificate, true);
        }

        if (!errorMessage.isEmpty()) {
            throw new MethodArgumentNotValidException(
                    ERROR_CODE_METHOD_ARGUMENT_NOT_VALID + ERROR_CODE_ORDER_NOT_VALID, errorMessage);
        }
    }

    private void checkEmptyFields(Order order, List<String> errorMessage) {
        if (order.getName() == null) {
            errorMessage.add(translator.toLocale("THE_NAME_FIELD_SHOULD_NOT_BE_EMPTY"));
        }

        if (order.getUser() == null) {
            errorMessage.add(translator.toLocale("THE_USER_FIELD_SHOULD_NOT_BE_EMPTY"));
        }

        if (order.getCertificates() == null) {
            errorMessage.add(translator.toLocale("THE_CERTIFICATES_FIELD_SHOULD_NOT_BE_EMPTY"));
        }

        if (!errorMessage.isEmpty()) {
            throw new MethodArgumentNotValidException(
                    ERROR_CODE_METHOD_ARGUMENT_NOT_VALID + ERROR_CODE_ORDER_NOT_VALID, errorMessage);
        }
    }

    /**
     * Checks whether a String corresponds UTF-8 format and its length is valid.
     *
     * @param toValidate is a String to validate.
     * @param maxLength  is a max length for the description.
     * @return true if the {@param toValidate} is in UTF-8 format and it length not more than {@param maxLength}.
     */
    @Override
    public boolean isNameValid(String toValidate, int maxLength) {
        byte[] byteArray = toValidate.getBytes();
        return isUTF8(byteArray) && !(toValidate.length() > maxLength);
    }
}

package com.epam.esm.validator.impl;

import com.epam.esm.configuration.Translator;
import com.epam.esm.exception.MethodArgumentNotValidException;
import com.epam.esm.model.impl.GiftCertificate;
import com.epam.esm.validator.CertificateValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class CertificateValidatorImpl implements CertificateValidator {
    public static final String ERROR_CODE_METHOD_ARGUMENT_NOT_VALID = "400";
    public static final String ERROR_CODE_CERTIFICATE_NOT_VALID = "01";

    private Translator translator;

    @Autowired
    public CertificateValidatorImpl(Translator translator) {
        this.translator = translator;
    }

    private static boolean isUTF8(final byte[] inputBytes) {
        final String converted = new String(inputBytes, StandardCharsets.UTF_8);
        final byte[] outputBytes = converted.getBytes(StandardCharsets.UTF_8);
        return Arrays.equals(inputBytes, outputBytes);
    }

    /**
     * Validates {@link GiftCertificate}.
     *
     * @param giftCertificate         is {@link GiftCertificate} to validate.
     * @param isEmptyFieldsAreChecked is the boolean to show whether the empty fields must be validated.
     */
    @Override
    public void validateCertificate(GiftCertificate giftCertificate, boolean isEmptyFieldsAreChecked) {
        List<String> errorMessage = new ArrayList<>();
        if (isEmptyFieldsAreChecked) {
            checkEmptyFields(giftCertificate, errorMessage);
        }

        if (giftCertificate.getName() != null && !isNameValid(giftCertificate.getName(), 30)) {
            errorMessage.add(translator.toLocale(
                    "CERTIFICATE_NAME_SHOULD_CONTAIN_ONLY_LATIN_LETTERS_AND_SHOULD_BE_NOT_MORE_THAN_30_SIGNS_LONG"));
        }

        if (giftCertificate.getName() != null && !isDescriptionValid(giftCertificate.getDescription(), 320)) {
            errorMessage.add(translator.toLocale(
                    "CERTIFICATE_DESCRIPTION_SHOULD_CONTAIN_ONLY_LATIN_LETTERS_AND_SHOULD_BE_NOT_MORE_THAN_320_SIGNS_LONG"));
        }

        if (giftCertificate.getPrice() != null && !isPriceValid(giftCertificate.getPrice())) {
            errorMessage.add(translator.toLocale("PRICE_CAN_NOT_BE_LESS_THAN_0"));
        }

        if (!(giftCertificate.getDuration() > 0)) {
            errorMessage.add(translator.toLocale("DURATION_SHOULD_BE_MORE_THAN_0"));
        }

        if (!errorMessage.isEmpty()) {
            throw new MethodArgumentNotValidException(
                    ERROR_CODE_METHOD_ARGUMENT_NOT_VALID + ERROR_CODE_CERTIFICATE_NOT_VALID, errorMessage);
        }
    }

    private void checkEmptyFields(GiftCertificate giftCertificate, List<String> errorMessage) {
        if (giftCertificate.getName() == null) {
            errorMessage.add(translator.toLocale("THE_NAME_FIELD_SHOULD_NOT_BE_EMPTY"));
        }

        if (giftCertificate.getDescription() == null) {
            errorMessage.add(translator.toLocale("THE_DESCRIPTION_FIELD_SHOULD_NOT_BE_EMPTY"));
        }

        if (giftCertificate.getPrice() == null) {
            errorMessage.add(translator.toLocale("THE_PRICE_FIELD_SHOULD_NOT_BE_EMPTY"));
        }

        if (giftCertificate.getDuration() == 0) {
            errorMessage.add(translator.toLocale("THE_DURATION_FIELD_SHOULD_NOT_BE_EMPTY"));
        }

        if (!errorMessage.isEmpty()) {
            throw new MethodArgumentNotValidException(
                    ERROR_CODE_METHOD_ARGUMENT_NOT_VALID + ERROR_CODE_CERTIFICATE_NOT_VALID, errorMessage);
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

    /**
     * Checks if a price is valid.
     *
     * @param price is a BigDecimal to validate.
     * @return true if price is more than 0.
     */
    @Override
    public boolean isPriceValid(BigDecimal price) {
        return price.longValue() >= 0;
    }

    /**
     * Checks if a duration is valid.
     *
     * @param duration is a long to validate.
     * @return true if a duration is more than 0.
     */
    @Override
    public boolean isDurationValid(long duration) {
        return duration > 0;
    }

    /**
     * Checks if a date is valid.
     *
     * @param date      is a LocalDate to validate.
     * @param toCompare is a LocalDate to compare with.
     * @return true if a {@param date} not less than {@param toCompareP}.
     */
    @Override
    public boolean isDateValid(LocalDateTime date, LocalDateTime toCompare) {
        return date.isAfter(toCompare) || date.isEqual(toCompare);
    }

    /**
     * Checks if a description is valid.
     *
     * @param description is a String to validate.
     * @param maxLength   is a max length for the description.
     * @return true if a {@param descripption} is in UTF-8 and it length not more than {@param maxLength}.
     */
    @Override
    public boolean isDescriptionValid(String description, int maxLength) {
        byte[] byteArray = description.getBytes();
        return isUTF8(byteArray) && !(description.length() > maxLength);
    }
}

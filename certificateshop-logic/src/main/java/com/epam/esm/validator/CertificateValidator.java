package com.epam.esm.validator;

import com.epam.esm.model.impl.GiftCertificate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface CertificateValidator {
    /**
     * Validates {@link GiftCertificate}.
     *
     * @param giftCertificate is {@link GiftCertificate} to validate.
     * @param isEmptyFieldsAreChecked is the boolean to show whether the empty fields must be validated.
     */
    void validateCertificate(GiftCertificate giftCertificate, boolean isEmptyFieldsAreChecked);

    /**
     * Checks whether a String corresponds UTF-8 format and its length is valid.
     *
     * @param toValidate is a String to validate.
     * @param maxLength  is a max length for the description.
     * @return true if the {@param toValidate} is in UTF-8 format and it length not more than {@param maxLength}.
     */
    boolean isNameValid(String toValidate, int maxLength);

    /**
     * Checks if a price is valid.
     *
     * @param price is a BigDecimal to validate.
     * @return true if price is more than 0.
     */
    boolean isPriceValid(BigDecimal price);

    /**
     * Checks if a duration is valid.
     *
     * @param duration is a long to validate.
     * @return true if a duration is more than 0.
     */
    boolean isDurationValid(long duration);

    /**
     * Checks if a date is valid.
     *
     * @param date      is a LocalDate to validate.
     * @param toCompare is a LocalDate to compare with.
     * @return true if a {@param date} not less than {@param toCompareP}.
     */
    boolean isDateValid(LocalDateTime date, LocalDateTime toCompare);

    /**
     * Checks if a description is valid.
     *
     * @param description is a String to validate.
     * @param maxLength   is a max length for the description.
     * @return true if a {@param descripption} is in UTF-8 and it length not more than {@param maxLength}.
     */
    boolean isDescriptionValid(String description, int maxLength);
}

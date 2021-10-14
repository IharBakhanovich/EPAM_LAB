package com.epam.esm.validator;

import com.epam.esm.model.impl.CertificateTag;
import com.epam.esm.model.impl.GiftCertificate;


public interface TagValidator {
    /**
     * Validates {@link CertificateTag}.
     * @param certificateTag is {@link CertificateTag} to validate.
     * @param isEmptyFieldsAreChecked is true if empty field should be checked.
     */
    void validateTag(CertificateTag certificateTag, boolean isEmptyFieldsAreChecked);

    /**
     * Checks whether a name corresponds UTF-8 format and its length is valid.
     *
     * @param toValidate is a String to validate.
     * @param maxLength is a max length for name.
     * @return true if the {@param toValidate} is in UTF-8 format and it length not more than {@param maxLength}.
     */
    public boolean isNameValid(String toValidate, int maxLength);
}
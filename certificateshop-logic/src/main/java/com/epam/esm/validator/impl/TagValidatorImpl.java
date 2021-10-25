package com.epam.esm.validator.impl;

import com.epam.esm.configuration.Translator;
import com.epam.esm.exception.MethodArgumentNotValidException;
import com.epam.esm.model.impl.CertificateTag;
import com.epam.esm.validator.TagValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class TagValidatorImpl implements TagValidator {
    public static final String ERROR_CODE_METHOD_ARGUMENT_NOT_VALID = "400";
    public static final String ERROR_CODE_NOT_VALID_TAG = "02";

    private Translator translator;

    @Autowired
    public TagValidatorImpl(Translator translator) {
        this.translator = translator;
    }

    private static boolean isUTF8(final byte[] inputBytes) {
        final String converted = new String(inputBytes, StandardCharsets.UTF_8);
        final byte[] outputBytes = converted.getBytes(StandardCharsets.UTF_8);
        return Arrays.equals(inputBytes, outputBytes);
    }

    /**
     * Validates {@link CertificateTag}.
     *
     * @param certificateTag is {@link CertificateTag} to validate.
     */
    @Override
    public void validateTag(CertificateTag certificateTag, boolean isEmptyFieldsAreChecked) {
        List<String> errorMessage = new ArrayList<>();
        if (!isNameValid(certificateTag.getName(), 30)) {
            errorMessage.add(translator.toLocale("TAG_NAME_IS_NOT_VALID_ERROR_MESSAGE"));
        }

        if (isEmptyFieldsAreChecked) {
            checkEmptyFields(certificateTag, errorMessage);
        }

        if (!errorMessage.isEmpty()) {
            throw new MethodArgumentNotValidException(
                    ERROR_CODE_METHOD_ARGUMENT_NOT_VALID + ERROR_CODE_NOT_VALID_TAG, errorMessage);
        }
    }

    private void checkEmptyFields(CertificateTag certificateTag, List<String> errorMessage) {
        if (certificateTag.getName() == null) {
            errorMessage.add(translator.toLocale("THE_NAME_FIELD_SHOULD_NOT_BE_EMPTY"));
        }
    }

    /**
     * Checks whether a name corresponds UTF-8 format and its length is valid.
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

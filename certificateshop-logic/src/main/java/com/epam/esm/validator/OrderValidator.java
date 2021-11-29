package com.epam.esm.validator;

import com.epam.esm.model.impl.Order;

public interface OrderValidator {
    /**
     * Validates {@link Order}.
     *
     * @param order is {@link Order} to validate.
     * @param isEmptyFieldsAreChecked is the boolean to show whether the empty fields must be validated.
     */
    void validateOrder(Order order, boolean isEmptyFieldsAreChecked);

    /**
     * Checks whether a String corresponds UTF-8 format and its length is valid.
     *
     * @param toValidate is a String to validate.
     * @param maxLength  is a max length for the description.
     * @return true if the {@param toValidate} is in UTF-8 format and it length not more than {@param maxLength}.
     */
    boolean isNameValid(String toValidate, int maxLength);
}

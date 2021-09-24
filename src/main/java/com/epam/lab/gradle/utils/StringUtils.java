package com.epam.lab.gradle.utils;

import com.epam.lab.gradle.utils.impl.StringUtilsImpl;

/**
 * An interface to realise the definition of the inputs.
 * Contains method 'isPositiveNumber' to define whether the input a positive number.
 */
public interface StringUtils {

    /**
     * Checks if the input a positive number.
     *
     * @param input is the checked input.
     * @return true if the {@param input} is a positive number.
     */
    boolean isPositiveNumber(String input);

    static StringUtils retrieve() {
        return StringUtilsImpl.getInstance();
    }
}

package com.epam.lab.gradle.utils;

import com.epam.lab.gradle.utils.impl.StringArrayUtilsImpl;

/**
 * An interface to realise the checking inputs utils.
 * Contains method 'isAllPositiveNumber' to define whether all the inputs are positive numbers.
 */
public interface StringArrayUtils {

    /**
     * Checks whether all the inputs are positive numbers.
     * The method does not cuts spaces at the beginning and at the end of the inputs?
     * so for inputs " 46" and "46 " and " 46 " it returns {@code false}.
     *
     * @param strings is the checked inputs.
     * @return {@code true} if the {@param strings} all positive numbers.
     * @author Ihar Bakhanovich
     */
    boolean isAllPositiveNumbers(String... strings);

    static com.epam.lab.gradle.utils.StringArrayUtils retrieve() {
        return StringArrayUtilsImpl.getInstance();
    }
}

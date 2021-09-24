package com.epam.lab.gradle.utils.impl;

import com.epam.lab.gradle.utils.StringArrayUtils;
import com.epam.lab.gradle.utils.StringUtils;

/**
 * An implementation of the StringArrayUtils interface. The Singleton.
 */
public class StringArrayUtilsImpl implements StringArrayUtils {

    private StringArrayUtilsImpl() {
    }

    private static class StringArrayUtilsImplHolder {
        private final static StringArrayUtilsImpl instance
                = new StringArrayUtilsImpl();
    }

    /**
     * Returns the instance of this class.
     *
     * @return Object of this class.
     */
    public static StringArrayUtilsImpl getInstance() {
        return StringArrayUtilsImpl.StringArrayUtilsImplHolder.instance;
    }


    /**
     * Checks whether all the inputs are positive numbers.
     * The method does not cuts spaces at the beginning and at the end of the inputs?
     * so for inputs " 46" and "46 " and " 46 " it returns {@code false}.
     *
     * @param strings is the checked inputs.
     * @return {@code true} if the {@param strings} all positive numbers.
     * @author Ihar Bakhanovich
     */
    @Override
    public boolean isAllPositiveNumbers(String... strings) {
        StringUtils stringUtils = StringUtils.retrieve();

        // to check the input on null
        if (strings == null) {
            return false;
        }

        for (String string : strings
        ) {
            if (!stringUtils.isPositiveNumber(string)) {
                return false;
            }
        }

        return true;
    }
}

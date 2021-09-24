package com.epam.lab.gradle.utils.impl;

import com.epam.lab.gradle.utils.StringUtils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import static org.apache.commons.lang3.StringUtils.*;
import static org.apache.commons.lang3.math.NumberUtils.isCreatable;
import static org.apache.commons.lang3.math.NumberUtils.isParsable;

/**
 * An implementation of the StringUtils interface. The Singleton.
 */
public class StringUtilsImpl implements StringUtils {

    private StringUtilsImpl() {
    }

    private static class StringUtilsImplHolder {
        private final static StringUtilsImpl instance
                = new StringUtilsImpl();
    }

    /**
     * Returns the instance of this class.
     *
     * @return Object of this class.
     */
    public static StringUtilsImpl getInstance() {
        return StringUtilsImpl.StringUtilsImplHolder.instance;
    }


    /**
     * Checks if the input a positive number.
     * The method does not cuts spaces at the beginning and at the end of the input?
     * so for inputs " 46" and "46 " and " 46 " it returns {@code false}.
     *
     * @param input is the checked input.
     * @return {@code true} if the {@param input} is a positive number.
     *
     * @author Ihar Bakhanovich
     */
    @Override
    public boolean isPositiveNumber(String input) {

        // to check the input on null
        if (input == null) {
            return false;
        }

        // to define decimal separator of the system
        DecimalFormat format = (DecimalFormat) DecimalFormat.getInstance();
        DecimalFormatSymbols symbols = format.getDecimalFormatSymbols();
        char separator = symbols.getDecimalSeparator();

        // counts the amount of separators
        int separatorsAmountInInput = countMatches(input, separator);

        // checks conditions of positive number
        if (separatorsAmountInInput > 1) {
            return false;
        } else if (separatorsAmountInInput == 0) {
            return isGradedAsNumberByCommonLang(input) && isFirstSymbolNotMinus(input);
        } else {
            return isInputAPositiveNumberWithFractional(input, separator);
        }
    }

    private boolean isGradedAsNumberByCommonLang(String input) {
        CharSequence charSequence = input;
        return (isCreatable(input)
                || isParsable(input)
                || isNumeric(charSequence));
    }

    private boolean isInputAPositiveNumberWithFractional(String input, char separator) {
        if (input == null) {
            return false;
        }
        String[] arrayOfParts = split(input, separator);
        if (arrayOfParts.length != 2) {
            return false;
        } else {
            return (
                    isGradedAsNumberByCommonLang(arrayOfParts[0])
                            && isFirstSymbolNotMinus(arrayOfParts[0])
                            && isGradedAsNumberByCommonLang(arrayOfParts[1])
                            && isFirstSymbolNotMinus(arrayOfParts[1])
            );
        }
    }

    private boolean isFirstSymbolNotMinus(String input) {
        return input.charAt(0) != '-';
    }
}

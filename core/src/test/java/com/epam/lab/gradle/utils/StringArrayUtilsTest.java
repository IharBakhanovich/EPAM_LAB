package com.epam.lab.gradle.utils;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StringArrayUtilsTest {
    @Test
    void isAllPositiveNumbersTest1() {
        StringArrayUtils util = StringArrayUtils.retrieve();
        assertFalse(util.isAllPositiveNumbers("12", "19", "-10"));
    }

//    // to test NumberUtils.isParsable(String)
//    @Test
//    void isPositiveNumberTest2() {
//        StringUtils util = StringUtils.retrieve();
//        assertTrue(util.isPositiveNumber("22"));
//    }
//
//    @Test
//    void isPositiveNumberTest3() {
//        StringUtils util = StringUtils.retrieve();
//        assertFalse(util.isPositiveNumber("-23"));
//    }
//
//    @Test
//    void isPositiveNumberTest4() {
//        StringUtils util = StringUtils.retrieve();
//        assertTrue(util.isPositiveNumber("2.2"));
//    }
//
//    @Test
//    void isPositiveNumberTest5() {
//        StringUtils util = StringUtils.retrieve();
//        assertTrue(util.isPositiveNumber("09"));
//    }
//
//    @Test
//    void isPositiveNumberTest6() {
//        StringUtils util = StringUtils.retrieve();
//        assertFalse(util.isPositiveNumber(null));
//    }
//
//    @Test
//    void isPositiveNumberTest7() {
//        StringUtils util = StringUtils.retrieve();
//        assertTrue(util.isPositiveNumber("6.2f"));
//    }
//
//    @Test
//    void isPositiveNumberTest8() {
//        StringUtils util = StringUtils.retrieve();
//        assertTrue(util.isPositiveNumber("9.8d"));
//    }
//
//    @Test
//    void isPositiveNumberTest9() {
//        StringUtils util = StringUtils.retrieve();
//        assertTrue(util.isPositiveNumber("22L"));
//    }
//
//    @Test
//    void isPositiveNumberTest10() {
//        StringUtils util = StringUtils.retrieve();
//        assertTrue(util.isPositiveNumber("0xFF"));
//    }
//
//    @Test
//    void isPositiveNumberTest11() {
//        StringUtils util = StringUtils.retrieve();
//        assertTrue(util.isPositiveNumber("2.99e+8"));
//    }
//
//    // to test NumberUtils.isCreatable(String)
//
//    @Test
//    void isPositiveNumberTest12() {
//        StringUtils util = StringUtils.retrieve();
//        assertTrue(util.isPositiveNumber("0xFFFFFFF"));
//    }
//
//    @Test
//    void isPositiveNumberTest13() {
//        StringUtils util = StringUtils.retrieve();
//        assertFalse(util.isPositiveNumber("-200"));
//    }
//
//    @Test
//    void isPositiveNumberTest14() {
//        StringUtils util = StringUtils.retrieve();
//        assertTrue(util.isPositiveNumber("10.0d"));
//    }
//
//    @Test
//    void isPositiveNumberTest15() {
//        StringUtils util = StringUtils.retrieve();
//        assertTrue(util.isPositiveNumber("1000L"));
//    }
//
//    @Test
//    void isPositiveNumberTest16() {
//        StringUtils util = StringUtils.retrieve();
//        assertTrue(util.isPositiveNumber("07"));
//    }
//
//    @Test
//    void isPositiveNumberTest17() {
//        StringUtils util = StringUtils.retrieve();
//        assertTrue(util.isPositiveNumber("2.99e+8"));
//    }
//
//    @Test
//    void isPositiveNumberTest18() {
//        StringUtils util = StringUtils.retrieve();
//        assertFalse(util.isPositiveNumber("abc"));
//    }
//
//    @Test
//    void isPositiveNumberTest19() {
//        StringUtils util = StringUtils.retrieve();
//        assertFalse(util.isPositiveNumber(" 22 "));
//    }
//
//    // The input is O9. The O is the letter
//    @Test
//    void isPositiveNumberTest20() {
//        StringUtils util = StringUtils.retrieve();
//        assertFalse(util.isPositiveNumber("O9"));
//    }
//
//    // to test StringUtils.isNumeric(CharSequence)
//
}

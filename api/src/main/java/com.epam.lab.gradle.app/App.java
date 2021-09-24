package com.epam.lab.gradle.app;

import com.epam.lab.gradle.utils.StringArrayUtils;

/**
 * A main class of the application.
 */
public class App {

    public static void main(String[] args) {
        StringArrayUtils util = StringArrayUtils.retrieve();
        System.out.println(util.isAllPositiveNumbers("12", "79"));
    }
}
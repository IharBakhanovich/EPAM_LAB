package com.epam.lab.gradle.app;

import com.epam.lab.gradle.utils.StringUtils;

/**
 * A main class of the application.
 */
public class App {

//    public String getGreeting() {
//        return "Hello World!";
//    }

    public static void main(String[] args) {
        StringUtils util = StringUtils.retrieve();
        System.out.println(util.isPositiveNumber(args[0]));
    }
}

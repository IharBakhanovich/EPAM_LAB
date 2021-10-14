package com.epam.esm.main;

import com.epam.esm.configuration.LogicConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class App {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context
                = new AnnotationConfigApplicationContext(LogicConfig.class);
        context.getBean("dataSource");
    }
}

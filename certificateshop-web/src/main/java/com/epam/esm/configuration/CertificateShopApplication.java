package com.epam.esm.configuration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@Import({LogicConfig.class, WebConfig.class})
@SpringBootApplication
@ComponentScan(basePackages="com.epam.esm")
public class CertificateShopApplication {
    public static void main(String[] args) {
        SpringApplication.run(CertificateShopApplication.class, args);
    }
}

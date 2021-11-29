package com.epam.esm.configuration;

import com.epam.esm.converter.OrderToOrderDtoConverter;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * The WebConfig class.
 */
@Configuration
@ComponentScan("com.epam.esm")
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {
}

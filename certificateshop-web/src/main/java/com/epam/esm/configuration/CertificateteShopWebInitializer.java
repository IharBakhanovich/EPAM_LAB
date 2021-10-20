package com.epam.esm.configuration;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

/**
 * The app initializer.
 */
public class CertificateteShopWebInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
    @Override
    protected Class<?>[] getRootConfigClasses() {
        return null;
    }

    /**
     * Defines {@link javax.servlet.ServletConfig} classes.
     *
     * @return array with all config classes which is to investigate.
     */
    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[]{LogicConfig.class, WebConfig.class};
    }

    /**
     * Defines ServletMapping.
     *
     * @return the mapping of the {@link javax.servlet.Servlet}.
     */
    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }
}

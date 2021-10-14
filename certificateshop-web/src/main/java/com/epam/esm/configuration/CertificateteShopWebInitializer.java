package com.epam.esm.configuration;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class CertificateteShopWebInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
    @Override
    protected Class<?>[] getRootConfigClasses() {
        return null;
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[]{LogicConfig.class, WebConfig.class};
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

//    @Override
//    public void onStartup(ServletContext servletContext) throws ServletException {
//
//        servletContext.setInitParameter(
//                "spring.profiles.active", "dev");
//    }

    //    env.setActiveProfiles("dev");
}
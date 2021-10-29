package com.epam.esm.configuration;

import com.epam.esm.converter.OrderToOrderDtoConverter;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.format.FormatterRegistry;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * The LogicConfig class.
 */
@Configuration
@ComponentScan("com.epam.esm")
@PropertySource("classpath:jdbc.properties")
public class LogicConfig extends AcceptHeaderLocaleResolver
        implements WebMvcConfigurer {
    private static final String DRIVER_PROPERTY_NAME = "JDBC_DRIVER";
    private static final String DB_MYSQL_PATH_PROPERTY_NAME = "DB_MYSQL_PATH";
    private static final String DB_SERVER_PROPERTY_NAME = "DB_SERVER";
    private static final String DB_PORT_PROPERTY_NAME = "DB_PORT";
    private static final String DB_NAME_PROPERTY_NAME = "DB_NAME";
    private static final String DB_USER_PROPERTY_NAME = "DB_USER";
    private static final String DB_PASSWORD_PROPERTY_NAME = "DB_PASSWORD";
    private static final String DB_MAX_CONNECTIONS_PROPERTY_NAME = "DB_MAX_CONNECTIONS";
    private final Environment environment;
    List<Locale> LOCALES = Arrays.asList(
            new Locale("en"),
            new Locale("de"),
            new Locale("ru"));

    @Autowired
    public LogicConfig(Environment environment) {
        this.environment = environment;
    }

    @Profile("test")
    @Bean(destroyMethod = "shutdown")
    public EmbeddedDatabase dataSourceTest() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript("sql/create_db.sql")
                .addScript("sql/insert_data.sql")
                .build();
    }

    @Profile("dev")
    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(environment.getRequiredProperty(DRIVER_PROPERTY_NAME));
        config.setJdbcUrl(environment.getRequiredProperty(DB_MYSQL_PATH_PROPERTY_NAME)
                + environment.getRequiredProperty(DB_SERVER_PROPERTY_NAME)
                + ":"
                + environment.getRequiredProperty(DB_PORT_PROPERTY_NAME)
                + "/"
                + environment.getRequiredProperty(DB_NAME_PROPERTY_NAME));
        config.setUsername(environment.getRequiredProperty(DB_USER_PROPERTY_NAME));
        config.setPassword(environment.getRequiredProperty(DB_PASSWORD_PROPERTY_NAME));
        config.setMaximumPoolSize(environment.getProperty(DB_MAX_CONNECTIONS_PROPERTY_NAME, Integer.class, 30));
        return new HikariDataSource(config);
    }

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        String headerLang = request.getHeader("Accept-Language");
        return headerLang == null || headerLang.isEmpty()
                ? Locale.getDefault()
                : Locale.lookup(Locale.LanguageRange.parse(headerLang), LOCALES);
    }

    @Bean
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource rs = new ResourceBundleMessageSource();
        rs.setBasename("messages");
        rs.setDefaultEncoding("UTF-8");
        rs.setUseCodeAsDefaultMessage(true);
        return rs;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new OrderToOrderDtoConverter());
    }
}

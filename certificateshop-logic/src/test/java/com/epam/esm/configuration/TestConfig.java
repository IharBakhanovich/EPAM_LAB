package com.epam.esm.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

/**
 * The Configuration for the DAO testing.
 */
@Configuration
@ComponentScan("com.epam.esm")
public class TestConfig extends LogicConfig {

    public TestConfig(Environment environment) {
        super(environment);
    }

    /**
     * the Embedded {@link javax.sql.DataSource} that is used by the DAO testing.
     *
     * @return {@link javax.sql.DataSource}.
     */
//    @Override
    @Bean(destroyMethod = "shutdown")
    public EmbeddedDatabase dataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript("sql/create_db.sql")
                .addScript("sql/insert_data.sql")
                .build();
    }
}

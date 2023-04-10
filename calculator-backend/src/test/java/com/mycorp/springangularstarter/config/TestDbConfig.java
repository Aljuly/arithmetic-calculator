package com.mycorp.springangularstarter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.mycorp.arithmeticcalculator.PersistenceJPAConfig;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@Primary
@ComponentScan({ "com.mycorp.arithmeticcalculator.repository" })
public class TestDbConfig extends PersistenceJPAConfig {

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder(11);
    }

    @Bean
    public DataSource dataSource() {
        EmbeddedDatabase datasource = new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2).build();
        return datasource;
    }

    @Override
    protected Properties additionalProperties() {
        Properties properties = super.additionalProperties();
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        properties.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        properties.setProperty("hibernate.show_sql", "true");
        return properties;
    }
}
package com.shadow2y.luthen.service.repository;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.shadow2y.luthen.service.AppConfig;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class LuthenHibernateBundle extends HibernateBundle<AppConfig> {

    public LuthenHibernateBundle() {
        super(
                Entity.class
        );
    }

    @Valid
    @NotNull
    @JsonProperty("database")
    private final DataSourceFactory database = new DataSourceFactory();

    public DataSourceFactory getDataSourceFactory() {
        return database;
    }

    @Override
    public DataSourceFactory getDataSourceFactory(AppConfig configuration) {
        return getDataSourceFactory();
    }

    @Override
    protected void configure(org.hibernate.cfg.Configuration configuration) {
        // Additional Hibernate configuration
        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        configuration.setProperty("hibernate.hbm2ddl.auto", "validate");
        configuration.setProperty("hibernate.show_sql", "false");
        configuration.setProperty("hibernate.format_sql", "true");
        configuration.setProperty("hibernate.use_sql_comments", "true");
        configuration.setProperty("hibernate.jdbc.batch_size", "20");
        configuration.setProperty("hibernate.order_inserts", "true");
        configuration.setProperty("hibernate.order_updates", "true");
        configuration.setProperty("hibernate.jdbc.batch_versioned_data", "true");

        // Connection pool settings
        configuration.setProperty("hibernate.c3p0.min_size", "5");
        configuration.setProperty("hibernate.c3p0.max_size", "20");
        configuration.setProperty("hibernate.c3p0.timeout", "300");
        configuration.setProperty("hibernate.c3p0.max_statements", "50");
        configuration.setProperty("hibernate.c3p0.idle_test_period", "3000");
    }
}

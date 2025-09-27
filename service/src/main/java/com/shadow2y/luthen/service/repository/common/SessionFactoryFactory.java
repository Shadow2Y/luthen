package com.shadow2y.luthen.service.repository.common;

import io.dropwizard.db.DataSourceFactory;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Map;

public class SessionFactoryFactory {

    public static SessionFactory create(
            DataSourceFactory dataSourceFactory,
            String dialect,
            Class<?>... entityClasses
    ) {
        return create(dataSourceFactory, dialect, List.of(entityClasses));
    }

    public static SessionFactory create(
            DataSourceFactory dataSourceFactory,
            String dialect,
            List<Class<?>> entityClasses
    ) {
        var configuration = new org.hibernate.cfg.Configuration();

        // Configure data source
        configureDataSource(configuration, dataSourceFactory);

        // Configure Hibernate
        configureHibernate(configuration, dialect);

        // Add entities
        entityClasses.forEach(configuration::addAnnotatedClass);

        return configuration.buildSessionFactory();
    }

    private static void configureDataSource(
            org.hibernate.cfg.Configuration config,
            DataSourceFactory dataSourceFactory
    ) {
        config.setProperty("hibernate.connection.driver_class", dataSourceFactory.getDriverClass());
        config.setProperty("hibernate.connection.url", dataSourceFactory.getUrl());
        config.setProperty("hibernate.connection.username", dataSourceFactory.getUser());
        config.setProperty("hibernate.connection.password", dataSourceFactory.getPassword());

        // Connection pool from DataSourceFactory
        config.setProperty("hibernate.connection.provider_class",
                "org.hibernate.hikaricp.internal.HikariCPConnectionProvider");
        config.setProperty("hibernate.hikari.minimumIdle", String.valueOf(dataSourceFactory.getMinSize()));
        config.setProperty("hibernate.hikari.maximumPoolSize", String.valueOf(dataSourceFactory.getMaxSize()));
    }

    private static void configureHibernate(
            org.hibernate.cfg.Configuration config,
            String dialect
    ) {
        var properties = Map.of(
                "hibernate.dialect", dialect,
                "hibernate.hbm2ddl.auto", "validate",
                "hibernate.show_sql", "false",
                "hibernate.format_sql", "true",
                "hibernate.use_sql_comments", "true",
                "hibernate.jdbc.batch_size", "25",
                "hibernate.order_inserts", "true",
                "hibernate.order_updates", "true",
                "hibernate.connection.autocommit", "false",
                "hibernate.current_session_context_class", "thread"
        );

        properties.forEach(config::setProperty);
    }
}


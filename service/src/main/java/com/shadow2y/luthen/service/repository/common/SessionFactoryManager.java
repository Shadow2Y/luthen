package com.shadow2y.luthen.service.repository.common;


import com.shadow2y.luthen.service.AppConfig;
import com.shadow2y.luthen.service.repository.tables.User;
import lombok.Getter;
import org.hibernate.SessionFactory;

public class SessionFactoryManager {

    @Getter
    private static SessionFactory sessionFactory;

    public static SessionFactory createSessionFactory(AppConfig config) {
        try {
            var configuration = new org.hibernate.cfg.Configuration();

            // Database connection settings
            var dataSourceFactory = config.getDatabase();
            configuration.setProperty("hibernate.connection.driver_class", dataSourceFactory.getDriverClass());
            configuration.setProperty("hibernate.connection.url", dataSourceFactory.getUrl());
            configuration.setProperty("hibernate.connection.username", dataSourceFactory.getUser());
            configuration.setProperty("hibernate.connection.password", dataSourceFactory.getPassword());

            // Hibernate settings
            configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
            configuration.setProperty("hibernate.hbm2ddl.auto", "validate");
            configuration.setProperty("hibernate.show_sql", "true");
            configuration.setProperty("hibernate.format_sql", "true");
            configuration.setProperty("hibernate.current_session_context_class", "thread");

            // Connection pool (HikariCP)
            configuration.setProperty("hibernate.connection.provider_class",
                    "com.zaxxer.hikari.hibernate.HikariConnectionProvider");
            configuration.setProperty("hibernate.hikari.minimumIdle", "5");
            configuration.setProperty("hibernate.hikari.maximumPoolSize", "20");
            configuration.setProperty("hibernate.hikari.idleTimeout", "300000");
            configuration.setProperty("hibernate.hikari.connectionTimeout", "20000");
            configuration.setProperty("hibernate.hikari.maxLifetime", "1200000");

            // Add annotated entity classes
            configuration.addAnnotatedClass(User.class);

            // Build SessionFactory
            sessionFactory = configuration.buildSessionFactory();
            return sessionFactory;

        } catch (Exception ex) {
            System.err.println("Failed to create SessionFactory: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}


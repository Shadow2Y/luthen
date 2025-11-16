package com.shadow2y.luthen.service.health;

import com.codahale.metrics.health.HealthCheck;
import jakarta.inject.Inject;
import org.hibernate.SessionFactory;

public class DatabaseHealthCheck extends HealthCheck {

    private final SessionFactory sessionFactory;

    @Inject
    public DatabaseHealthCheck(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    protected Result check() throws Exception {
        try (var session = sessionFactory.openSession()) {
            // Simple query to test connection
            var result = session.createNativeQuery("SELECT 1", Integer.class)
                    .uniqueResult();

            return result != null && result == 1
                    ? Result.healthy("Database connection is working")
                    : Result.unhealthy("Database query returned unexpected result");

        } catch (Exception e) {
            return Result.unhealthy("Cannot connect to database: " + e.getMessage());
        }
    }
}

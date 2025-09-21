package com.shadow2y.luthen.service.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.function.Consumer;
import java.util.function.Function;

public class HibernateUtil {

    private final SessionFactory sessionFactory;

    public HibernateUtil(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    // Execute with transaction
    public <T> T executeInTransaction(Function<Session, T> operation) {
        try (var session = sessionFactory.openSession()) {
            var transaction = session.beginTransaction();
            try {
                var result = operation.apply(session);
                transaction.commit();
                return result;
            } catch (Exception e) {
                transaction.rollback();
                throw new RuntimeException("Transaction failed", e);
            }
        }
    }

    // Execute without return value
    public void executeInTransaction(Consumer<Session> operation) {
        executeInTransaction(session -> {
            operation.accept(session);
            return null;
        });
    }

    // Get current session (requires proper session context)
    public Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    // Create a new session
    public Session openSession() {
        return sessionFactory.openSession();
    }

    // Statistics and monitoring
    public record SessionStats(
            long openSessionCount,
            long closedSessionCount,
            long transactionCount,
            long successfulTransactionCount
    ) {}

    public SessionStats getStats() {
        var stats = sessionFactory.getStatistics();
        return new SessionStats(
                stats.getSessionOpenCount(),
                stats.getSessionCloseCount(),
                stats.getTransactionCount(),
                stats.getSuccessfulTransactionCount()
        );
    }
}

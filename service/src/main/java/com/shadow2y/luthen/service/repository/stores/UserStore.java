package com.shadow2y.luthen.service.repository.stores;

import com.shadow2y.luthen.service.repository.tables.User;
import io.dropwizard.hibernate.AbstractDAO;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.hibernate.SessionFactory;

import java.util.Optional;

@Singleton
public class UserStore extends AbstractDAO<User> {

    @PersistenceContext
    private EntityManager em;

    @Inject
    public UserStore(SessionFactory factory) {
        super(factory);
    }

    @Transactional
    public User save(User user) {
        em.persist(user);
        return user;
    }

    public Optional<User> findByUsername(String username) {
        return em.createNamedQuery("User.findByUsername", User.class)
                .setParameter("username", username)
                .getResultStream()
                .findFirst();
    }

    public boolean existsByUsername(String username) {
        Long count = em.createQuery("SELECT COUNT(u) FROM User u WHERE u.username = :username", Long.class)
                .setParameter("username", username)
                .getSingleResult();
        return count > 0;
    }

    public Optional<User> findByEmail(String email) {
        return em.createNamedQuery("User.findByEmail", User.class)
                .setParameter("email", email)
                .getResultStream()
                .findFirst();
    }

    public boolean existsByEmail(String emailId) {
        Long count = em.createQuery("SELECT COUNT(u) FROM User u WHERE u.email = :email", Long.class)
                .setParameter("email", emailId)
                .getSingleResult();
        return count > 0;
    }

    public User create(User user) {
        return persist(user);
    }

    public User update(User user) {
        return persist(user);
    }

    @Transactional
    public void delete(User user) {
        em.remove(em.contains(user) ? user : em.merge(user));
    }


}
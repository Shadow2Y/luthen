package com.shadow2y.luthen.service.repository.stores;

import com.shadow2y.luthen.service.repository.tables.User;
import io.dropwizard.hibernate.AbstractDAO;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import org.hibernate.SessionFactory;

import java.util.Optional;
import java.util.logging.Logger;

@Singleton
public class UserStore extends AbstractDAO<User> {
    private static final Logger log = Logger.getLogger(UserStore.class.getName());

    @Inject
    public UserStore(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Transactional
    public User save(User user) {
        log.info("Saving user :: " + user);
        persist(user);
        return user;
    }

    public User save(String username, String email, String hashedPassword) {
        User user = new User(username,email,hashedPassword);
        return save(user);
    }

    public Optional<User> findByUsername(String username) {
        return currentSession()
                .createNamedQuery("User.findByUsername", User.class)
                .setParameter("username", username)
                .getResultStream()
                .findFirst();
    }

    public boolean existsByUsername(String username) {
        Long count = currentSession()
                .createQuery("SELECT COUNT(u) FROM User u WHERE u.username = :username", Long.class)
                .setParameter("username", username)
                .getSingleResult();
        return count > 0;
    }

    public Optional<User> findByEmail(String email) {
        return currentSession()
                .createNamedQuery("User.findByEmail", User.class)
                .setParameter("email", email)
                .getResultStream()
                .findFirst();
    }

    public boolean existsByEmail(String emailId) {
        Long count = currentSession()
                .createQuery("SELECT COUNT(u) FROM User u WHERE u.email = :email", Long.class)
                .setParameter("email", emailId)
                .getSingleResult();
        return count > 0;
    }

    public User update(User user) {
        log.info("Updating user: " + user);
        return currentSession().merge(user);
    }

    public void delete(User user) {
        log.info("Deleting user: " + user);
        currentSession().remove(currentSession().contains(user) ? user : currentSession().merge(user));
    }

}
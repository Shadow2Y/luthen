package com.shadow2y.luthen.service.repository.dao;

import com.shadow2y.luthen.service.repository.Entity;
import io.dropwizard.hibernate.AbstractDAO;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.hibernate.SessionFactory;

import java.util.Optional;

@Singleton
public class EntityDAO extends AbstractDAO<Entity> {

    @Inject
    public EntityDAO(SessionFactory factory) {
        super(factory);
    }

    public Optional<Entity> findById(Long id) {
        return Optional.ofNullable(get(id));
    }

    public Optional<Entity> findByMailId(String id) {
        return Optional.ofNullable(get(id));
    }

    public Entity create(Entity entity) {
        return persist(entity);
    }

    public void delete(Entity entity) {
        currentSession().delete(entity);
    }

    public Entity update(Entity entity) {
        return persist(entity);
    }

}
package com.shadow2y.luthen.service.repository.stores;

import io.dropwizard.hibernate.AbstractDAO;
import jakarta.inject.Inject;
import org.hibernate.SessionFactory;

public class BaseDAO<T> extends AbstractDAO<T> {

    @Inject
    public BaseDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

}

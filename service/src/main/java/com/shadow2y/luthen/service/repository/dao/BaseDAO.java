package com.shadow2y.luthen.service.repository.dao;

import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;

public abstract class BaseDAO<T> extends AbstractDAO<T> {

    public BaseDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

}

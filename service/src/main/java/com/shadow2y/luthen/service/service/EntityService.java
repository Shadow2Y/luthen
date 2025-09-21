package com.shadow2y.luthen.service.service;

import com.shadow2y.luthen.service.repository.dao.EntityDAO;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class EntityService {

    private final EntityDAO entityDAO;

    @Inject
    public EntityService(EntityDAO entityDAO) {
        this.entityDAO = entityDAO;
    }

    public boolean isUserNew(String emailId) {
        return entityDAO.findByMailId(emailId).isEmpty();
    }

}

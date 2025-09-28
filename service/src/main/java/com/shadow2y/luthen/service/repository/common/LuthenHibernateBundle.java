package com.shadow2y.luthen.service.repository.common;

import com.shadow2y.luthen.service.AppConfig;
import com.shadow2y.luthen.service.repository.tables.Permission;
import com.shadow2y.luthen.service.repository.tables.Role;
import com.shadow2y.luthen.service.repository.tables.User;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;

public class LuthenHibernateBundle extends HibernateBundle<AppConfig> {

    public LuthenHibernateBundle() {
        super(
                User.class,
                Role.class,
                Permission.class
        );
    }

    @Override
    public DataSourceFactory getDataSourceFactory(AppConfig configuration) {
        return configuration.getDatabase();
    }

}

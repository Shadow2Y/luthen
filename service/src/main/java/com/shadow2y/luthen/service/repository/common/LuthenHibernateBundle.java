package com.shadow2y.luthen.service.repository.common;

import com.shadow2y.luthen.service.AppConfig;
import com.shadow2y.luthen.service.repository.tables.Permission;
import com.shadow2y.luthen.service.repository.tables.Role;
import com.shadow2y.luthen.service.repository.tables.User;
import io.dropwizard.core.setup.Environment;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.hibernate.SessionFactory;

import java.util.List;

public class LuthenHibernateBundle extends HibernateBundle<AppConfig> {

    public LuthenHibernateBundle() {
        super(
                User.class,
                Role.class,
                Permission.class
        );
    }

    @Override
    public void run(AppConfig appConfig, Environment environment) throws Exception {
        super.run(appConfig, environment);
        environment.jersey().register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(getSessionFactory()).to(SessionFactory.class);
            }
        });
    }

    @Override
    public DataSourceFactory getDataSourceFactory(AppConfig configuration) {
        return configuration.database;
    }

}

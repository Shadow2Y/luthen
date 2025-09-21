package com.shadow2y.luthen.service;

import com.shadow2y.luthen.service.health.DatabaseHealthCheck;
import com.shadow2y.luthen.service.repository.LuthenHibernateBundle;
import com.shadow2y.luthen.service.repository.dao.EntityDAO;
import com.shadow2y.luthen.service.resource.EntityResource;
import com.shadow2y.luthen.service.service.EntityService;
import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.hibernate.SessionFactory;

public class App extends Application<AppConfig> {

    private final LuthenHibernateBundle hibernateBundle = new LuthenHibernateBundle();

    public static void main(String[] args) throws Exception {
        new App().run(args);
    }

    @Override
    public void initialize(Bootstrap<AppConfig> bootstrap) {
        bootstrap.addBundle(hibernateBundle);
    }

    @Override
    public void run(AppConfig appConfig, Environment environment) {
        var sessionFactory = hibernateBundle.getSessionFactory();

        registerDAOs(environment, sessionFactory);
        registerServices(environment);
        registerResources(environment);
        registerHealthChecks(environment, sessionFactory);
    }

    public void registerDAOs(Environment environment, SessionFactory sessionFactory) {
        environment.jersey().register(new EntityDAO(sessionFactory));
    }

    public void registerServices(Environment environment) {
        environment.jersey().register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(EntityService.class).to(EntityService.class);
            }
        });
    }

    public void registerResources(Environment environment) {
        environment.jersey().register(EntityResource.class);
    }

    public void registerHealthChecks(Environment environment, SessionFactory sessionFactory) {
        environment.healthChecks().register("database",
                new DatabaseHealthCheck(sessionFactory));
    }

}

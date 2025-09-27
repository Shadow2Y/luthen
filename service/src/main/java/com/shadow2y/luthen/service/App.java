package com.shadow2y.luthen.service;

import com.shadow2y.luthen.auth.LuthenBundle;
import com.shadow2y.luthen.service.health.DatabaseHealthCheck;
import com.shadow2y.luthen.service.repository.common.LuthenHibernateBundle;
import com.shadow2y.luthen.service.repository.stores.UserStore;
import com.shadow2y.luthen.service.resource.AuthResource;
import com.shadow2y.luthen.service.service.AuthService;
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

        environment.jersey().register(new LuthenBundle<AppConfig>(null));

        registerDAOs(environment, sessionFactory);
        registerServices(environment);
        registerResources(environment);
        registerHealthChecks(environment, sessionFactory);
    }

    public void registerDAOs(Environment environment, SessionFactory sessionFactory) {
        environment.jersey().register(new UserStore(sessionFactory));
    }

    public void registerServices(Environment environment) {
        environment.jersey().register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(AuthService.class).to(AuthService.class);
            }
        });
    }

    public void registerResources(Environment environment) {
        environment.jersey().register(AuthResource.class);
    }

    public void registerHealthChecks(Environment environment, SessionFactory sessionFactory) {
        environment.healthChecks().register("database",
                new DatabaseHealthCheck(sessionFactory));
    }

}

package com.shadow2y.luthen.service;

import com.shadow2y.luthen.auth.LuthenBundle;
import com.shadow2y.luthen.service.exception.LuthenExceptionMapper;
import com.shadow2y.luthen.service.health.DatabaseHealthCheck;
import com.shadow2y.luthen.service.managed.AsyncManager;
import com.shadow2y.luthen.service.repository.common.LuthenHibernateBundle;
import com.shadow2y.luthen.service.resource.*;
import com.shadow2y.luthen.service.service.*;
import com.shadow2y.luthen.service.utils.SerDe;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;


public class App extends Application<AppConfig> {

    private final LuthenHibernateBundle hibernateBundle;

    public App() {
        hibernateBundle = new LuthenHibernateBundle();
    }

    static void main(String[] args) throws Exception {
        new App().run(args);
    }

    @Override
    public void initialize(Bootstrap<AppConfig> bootstrap) {
        bootstrap.addBundle(hibernateBundle);
        bootstrap.addBundle(new LuthenBundle<>());
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(bootstrap.getConfigurationSourceProvider(), new EnvironmentVariableSubstitutor())
        );
        bootstrap.addBundle(new SwaggerBundle<>() {
            @Override protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(AppConfig configuration) {
                return configuration.swaggerBundleConfiguration;
        }});
    }

    @Override
    public void run(AppConfig config, Environment env) {
        SerDe.init(env.getObjectMapper());
        env.jersey().register(new LuthenBinder(config));

        setManaged(config,env);
        registerResources(env);
        registerHealthChecks(env);
    }

    public void setManaged(AppConfig config, Environment env) {
        AsyncManager executorManager = new AsyncManager(config.asyncExecutor);
        env.lifecycle().manage(executorManager);
    }

    public void registerHealthChecks(Environment environment) {
        environment.healthChecks().register("database", new DatabaseHealthCheck(hibernateBundle.getSessionFactory()));
    }
    
    public void registerResources(Environment environment) {
        environment.jersey().register(AuthResource.class);
        environment.jersey().register(TestResource.class);
        environment.jersey().register(HealthResource.class);
        environment.jersey().register(IdentityResource.class);
        environment.jersey().register(LuthenClientResource.class);
        environment.jersey().register(LuthenExceptionMapper.class);
    }

}

package com.shadow2y.luthen.service;

import com.shadow2y.luthen.service.exception.mapper.LuthenExceptionMapper;
import com.shadow2y.luthen.service.health.DatabaseHealthCheck;
import com.shadow2y.luthen.service.repository.common.LuthenHibernateBundle;
import com.shadow2y.luthen.service.repository.stores.PermissionStore;
import com.shadow2y.luthen.service.repository.stores.RoleStore;
import com.shadow2y.luthen.service.repository.stores.UserStore;
import com.shadow2y.luthen.service.resource.AuthResource;
import com.shadow2y.luthen.service.resource.HealthResource;
import com.shadow2y.luthen.service.service.AuthService;
import com.shadow2y.luthen.service.service.LuthenTokenService;
import com.shadow2y.luthen.service.service.PasswordServiceImpl;
import com.shadow2y.luthen.service.utils.CryptoUtils;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import org.hibernate.SessionFactory;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

public class App extends Application<AppConfig> {

    private final LuthenHibernateBundle hibernateBundle = new LuthenHibernateBundle();

    static void main(String[] args) throws Exception {
        new App().run(args);
    }

    @Override
    public void initialize(Bootstrap<AppConfig> bootstrap) {
        bootstrap.addBundle(hibernateBundle);
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(bootstrap.getConfigurationSourceProvider(), new EnvironmentVariableSubstitutor())
        );

        bootstrap.addBundle(new SwaggerBundle<>() {
            @Override protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(AppConfig configuration) {
                return configuration.swaggerBundleConfiguration;
        }});
    }

    @Override
    public void run(AppConfig appConfig, Environment environment) {

        CryptoUtils.init(appConfig.getAuthConfig().getFilterAlgorithm(), appConfig.getAuthConfig().getFilterSeed(), appConfig.getAuthConfig().getFilteringWindowMins());

        KeyPair keyPair = CryptoUtils.validateGenerateKeys(appConfig);
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

        var sessionFactory = hibernateBundle.getSessionFactory();

        registerResources(environment);
        registerHealthChecks(environment, sessionFactory);
        registerServices(environment, sessionFactory, publicKey, privateKey);
    }

    public void registerServices(Environment environment, SessionFactory sessionFactory, RSAPublicKey publicKey, RSAPrivateKey privateKey) {
        var userStore = new UserStore(sessionFactory);
        var roleStore = new RoleStore(sessionFactory);
        var permissionStore = new PermissionStore(sessionFactory);
        environment.jersey().register(userStore);

        var passwordService = new PasswordServiceImpl();
        var tokenService = new LuthenTokenService(privateKey, publicKey, "luthen", 10L);
        var authService = new AuthService(tokenService, passwordService, userStore, roleStore, permissionStore);

        environment.jersey().register(authService);
        environment.jersey().register(tokenService);
    }

    public void registerResources(Environment environment) {
        environment.jersey().register(AuthResource.class);
        environment.jersey().register(HealthResource.class);
        environment.jersey().register(new LuthenExceptionMapper());
    }

    public void registerHealthChecks(Environment environment, SessionFactory sessionFactory) {
        environment.healthChecks().register("database", new DatabaseHealthCheck(sessionFactory));
    }

}

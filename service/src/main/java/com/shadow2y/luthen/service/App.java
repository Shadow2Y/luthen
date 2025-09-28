package com.shadow2y.luthen.service;

import com.shadow2y.luthen.auth.LuthenBundle;
import com.shadow2y.luthen.service.exception.mapper.LuthenExceptionMapper;
import com.shadow2y.luthen.service.health.DatabaseHealthCheck;
import com.shadow2y.luthen.service.repository.common.LuthenHibernateBundle;
import com.shadow2y.luthen.service.repository.stores.UserStore;
import com.shadow2y.luthen.service.resource.AuthResource;
import com.shadow2y.luthen.service.resource.HealthResource;
import com.shadow2y.luthen.service.service.AuthService;
import com.shadow2y.luthen.service.service.LuthenTokenService;
import com.shadow2y.luthen.service.service.PasswordServiceImpl;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.hibernate.SessionFactory;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

public class App extends Application<AppConfig> {

    private final LuthenHibernateBundle hibernateBundle = new LuthenHibernateBundle();

    static void main(String[] args) throws Exception {
        new App().run(args);
    }

    @Override
    public void initialize(Bootstrap<AppConfig> bootstrap) {
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(bootstrap.getConfigurationSourceProvider(), new EnvironmentVariableSubstitutor())
        );
        bootstrap.addBundle(hibernateBundle);
        bootstrap.addBundle(new SwaggerBundle<>() {
            @Override protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(AppConfig configuration) {
                return configuration.swaggerBundleConfiguration;
            }
        });
    }

    @Override
    public void run(AppConfig appConfig, Environment environment) {
        var sessionFactory = hibernateBundle.getSessionFactory();

        environment.jersey().register(new LuthenBundle<AppConfig>(null));

        registerServices(environment, sessionFactory);
        registerResources(environment);
        registerHealthChecks(environment, sessionFactory);
    }

    public void registerServices(Environment environment, SessionFactory sessionFactory) {
        var entityManager = hibernateBundle.getSessionFactory().createEntityManager();
        var userStore = new UserStore(entityManager, sessionFactory);
        environment.jersey().register(userStore);

        // Generate test RSA key pair for dev/testing
        KeyPair keyPair = generateTestRsaKeyPair();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

        var passwordService = new PasswordServiceImpl();
        var tokenService = new LuthenTokenService(privateKey, publicKey, "luthen", 60L);
        var authService = new AuthService(userStore, passwordService, tokenService);

        environment.jersey().register(new AbstractBinder() {
            @Override protected void configure() {
                bind(authService).to(AuthService.class);
            }});
    }

    public void registerResources(Environment environment) {
        environment.jersey().register(AuthResource.class);
        environment.jersey().register(HealthResource.class);
        environment.jersey().register(new LuthenExceptionMapper());
    }

    public void registerHealthChecks(Environment environment, SessionFactory sessionFactory) {
        environment.healthChecks().register("database", new DatabaseHealthCheck(sessionFactory));
    }


    private static KeyPair generateTestRsaKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            return keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to generate RSA key pair", e);
        }
    }

}

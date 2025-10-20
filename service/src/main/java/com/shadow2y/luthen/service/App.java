package com.shadow2y.luthen.service;

import com.shadow2y.luthen.service.exception.LuthenExceptionMapper;
import com.shadow2y.luthen.service.general.AsyncExecutorManager;
import com.shadow2y.luthen.service.health.DatabaseHealthCheck;
import com.shadow2y.luthen.service.repository.common.LuthenHibernateBundle;
import com.shadow2y.luthen.service.repository.common.ValkeyFactory;
import com.shadow2y.luthen.service.repository.stores.OTPStore;
import com.shadow2y.luthen.service.repository.stores.PermissionStore;
import com.shadow2y.luthen.service.repository.stores.RoleStore;
import com.shadow2y.luthen.service.repository.stores.UserStore;
import com.shadow2y.luthen.service.resource.*;
import com.shadow2y.luthen.service.service.*;
import com.shadow2y.luthen.service.service.intf.PasswordService;
import com.shadow2y.luthen.service.service.intf.TokenService;
import com.shadow2y.luthen.service.utils.CryptoUtils;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import jakarta.mail.internet.AddressException;
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
    public void run(AppConfig config, Environment env) throws AddressException {

        KeyPair keyPair = CryptoUtils.validateGenerateKeys(config);
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

        var sessionFactory = hibernateBundle.getSessionFactory();

        setManaged(config, env);
        registerResources(env);
        registerHealthChecks(env, sessionFactory);
        registerServices(config, env, sessionFactory, publicKey, privateKey);
    }

    public void setManaged(AppConfig config, Environment env) {
        AsyncExecutorManager executorManager = new AsyncExecutorManager(config.asyncExecutor);
        env.lifecycle().manage(executorManager);
    }

    public void registerServices(AppConfig appConfig, Environment environment, SessionFactory sessionFactory, RSAPublicKey publicKey, RSAPrivateKey privateKey) throws AddressException {
        /* STORES */
        UserStore userStore = new UserStore(sessionFactory);
        RoleStore roleStore = new RoleStore(sessionFactory);
        PermissionStore permissionStore = new PermissionStore(sessionFactory);
        OTPStore otpStore = new OTPStore(ValkeyFactory.build(appConfig.identityConfig.valkeyConfig), appConfig.identityConfig.valkeyConfig.expiryInSeconds);

        environment.jersey().register(userStore);
        environment.jersey().register(roleStore);
        environment.jersey().register(permissionStore);
        environment.jersey().register(otpStore);

        /* SERVICE */
        PasswordService passwordService = new PasswordServiceImpl(appConfig.authConfig.getPasswordSaltRounds());
        TokenService tokenService = new LuthenTokenService(privateKey, publicKey, "luthen", appConfig.authConfig.getJwtExpiryMinutes());

        LuthenClientService clientService = new LuthenClientService(appConfig, roleStore, permissionStore);
        AuthService authService = new AuthService(tokenService, passwordService, userStore, roleStore, permissionStore);
        IdentityService identityService = new IdentityService(passwordService, userStore, otpStore, appConfig.identityConfig);

        environment.jersey().register(identityService);
        environment.jersey().register(authService);
        environment.jersey().register(clientService);
    }

    public void registerResources(Environment environment) {
        environment.jersey().register(AuthResource.class);
        environment.jersey().register(IdentityResource.class);
        environment.jersey().register(LuthenClientResource.class);
        environment.jersey().register(HealthResource.class);
        environment.jersey().register(TestResource.class);
        environment.jersey().register(new LuthenExceptionMapper());
    }

    public void registerHealthChecks(Environment environment, SessionFactory sessionFactory) {
        environment.healthChecks().register("database", new DatabaseHealthCheck(sessionFactory));
    }

}

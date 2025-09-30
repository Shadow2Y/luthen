package com.shadow2y.luthen.service;

import com.shadow2y.luthen.auth.LuthenBundle;
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
import java.util.Base64;

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

        KeyPair keyPair = validateGenerateKeys(new AppConfig());
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

        registerServices(environment, sessionFactory, publicKey, privateKey);
        registerResources(environment);
        registerHealthChecks(environment, sessionFactory);
    }

    public void registerServices(Environment environment, SessionFactory sessionFactory,
                                  RSAPublicKey publicKey, RSAPrivateKey privateKey) {
        var userStore = new UserStore(sessionFactory);
        var roleStore = new RoleStore(sessionFactory);
        var permissionStore = new PermissionStore(sessionFactory);
        environment.jersey().register(userStore);

        var passwordService = new PasswordServiceImpl();
        // Fix parameter order: privateKey first, then publicKey
        var tokenService = new LuthenTokenService(privateKey, publicKey, "luthen", 10L);
        var authService = new AuthService(tokenService, passwordService, userStore, roleStore, permissionStore);

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

    /// AUTH Logics

    public KeyPair validateGenerateKeys(AppConfig appConfig) {
        try {
            var publicKey = loadPublicKey(appConfig.getRsaPublicKey());
            var privateKey = loadPrivateKey(appConfig.getRsaPrivateKey());
            return new KeyPair(publicKey, privateKey);
        } catch (Exception e) {
            return generateTestRsaKeyPair();
        }
    }

    private static KeyPair generateTestRsaKeyPair() {
        KeyPair keyPair;
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            keyPair = keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to generate RSA key pair", e);
        }
        // Generate new keys and print them
        System.err.println("\n=============== RSA KEYS MISSING OR INVALID ===============");
        System.err.println("Generating new RSA key pair. Add these to your environment variables:\n");

        var publicKey = (RSAPublicKey) keyPair.getPublic();
        var privateKey = (RSAPrivateKey) keyPair.getPrivate();

        // Convert keys to Base64
        String publicKeyBase64 = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        String privateKeyBase64 = Base64.getEncoder().encodeToString(privateKey.getEncoded());

        System.err.println("export RSA_PUBLIC_KEY=\"" + publicKeyBase64 + "\"");
        System.err.println("export RSA_PRIVATE_KEY=\"" + privateKeyBase64 + "\"");
        System.err.println("\n=======================================================");

        throw new RuntimeException("RSA keys not configured. Generated new keys - see above for values to use.");
    }

    private RSAPublicKey loadPublicKey(String base64Key) {
        try {
            byte[] decoded = java.util.Base64.getDecoder().decode(base64Key);
            java.security.spec.X509EncodedKeySpec keySpec = new java.security.spec.X509EncodedKeySpec(decoded);
            java.security.KeyFactory kf = java.security.KeyFactory.getInstance("RSA");
            return (RSAPublicKey) kf.generatePublic(keySpec);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load RSA public key", e);
        }
    }

    private RSAPrivateKey loadPrivateKey(String base64Key) {
        try {
            byte[] decoded = java.util.Base64.getDecoder().decode(base64Key);
            java.security.spec.PKCS8EncodedKeySpec keySpec = new java.security.spec.PKCS8EncodedKeySpec(decoded);
            java.security.KeyFactory kf = java.security.KeyFactory.getInstance("RSA");
            return (RSAPrivateKey) kf.generatePrivate(keySpec);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load RSA private key", e);
        }
    }
}

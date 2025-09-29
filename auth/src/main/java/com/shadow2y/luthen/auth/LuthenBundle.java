package com.shadow2y.luthen.auth;

import com.shadow2y.luthen.api.response.UserAuth;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.oauth.OAuthCredentialAuthFilter;
import io.dropwizard.core.Configuration;
import io.dropwizard.core.ConfiguredBundle;
import io.dropwizard.core.setup.Environment;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

import java.security.interfaces.RSAPublicKey;

public class LuthenBundle<T extends Configuration> implements ConfiguredBundle<T> {

    private final RSAPublicKey publicKey;

    public LuthenBundle(RSAPublicKey publicKey) {
        this.publicKey = publicKey;
    }

    @Override
    public void run(T config, Environment env) {
        Authenticator<String,UserAuth> authenticator = new com.shadow2y.luthen.auth.Authenticator(publicKey,"luthen");
        RoleAuthorizer authorizer = new RoleAuthorizer();

        OAuthCredentialAuthFilter<UserAuth> filter =
                new OAuthCredentialAuthFilter.Builder<UserAuth>()
                        .setAuthenticator(authenticator)
                        .setAuthorizer(authorizer)
                        .setPrefix("Bearer")
                        .buildAuthFilter();

        env.jersey().register(new AuthDynamicFeature(filter));
        env.jersey().register(new AuthValueFactoryProvider.Binder<>(UserAuth.class));
        env.jersey().register(RolesAllowedDynamicFeature.class);
    }
}


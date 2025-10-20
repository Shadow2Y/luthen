package com.shadow2y.luthen.auth;

import com.shadow2y.luthen.auth.models.LuthenBundleConfig;
import com.shadow2y.luthen.auth.models.JWTWrap;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.oauth.OAuthCredentialAuthFilter;
import io.dropwizard.core.ConfiguredBundle;
import io.dropwizard.core.setup.Environment;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

public class LuthenBundle<T extends LuthenBundleConfig> implements ConfiguredBundle<T> {

    @Override
    public void run(T config, Environment env) {
        Authenticator<String, JWTWrap> authenticator = new LuthenAuthenticator(config.getPublicKey(), config.getIssuer());
        RoleAuthorizer authorizer = new RoleAuthorizer();

        OAuthCredentialAuthFilter<JWTWrap> filter =
                new OAuthCredentialAuthFilter.Builder<JWTWrap>()
                        .setAuthenticator(authenticator)
                        .setAuthorizer(authorizer)
                        .setPrefix("Bearer")
                        .setRealm("realm") // <== important!
                        .buildAuthFilter();

        env.jersey().register(new AuthDynamicFeature(filter));
        env.jersey().register(new AuthValueFactoryProvider.Binder<>(JWTWrap.class));
        env.jersey().register(RolesAllowedDynamicFeature.class);
    }
}


package com.shadow2y.luthen.service;

import com.shadow2y.luthen.auth.models.LuthenBundleConfig;
import com.shadow2y.luthen.auth.models.LuthenClientConfig;
import com.shadow2y.luthen.service.model.config.*;
import com.shadow2y.luthen.service.utils.CryptoUtils;
import io.dropwizard.core.Configuration;
import io.dropwizard.db.DataSourceFactory;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import java.security.KeyPair;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.util.Map;

@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AppConfig extends Configuration implements LuthenBundleConfig {

    @Valid
    public final ExecutorConfig asyncExecutor;

    @Valid @NotNull
    public final DataSourceFactory database;

    @Valid @NotNull
    public LuthenAuthConfig authConfig;

    @Valid @NotNull
    public IdentityConfig identityConfig;

    public SwaggerBundleConfiguration swaggerBundleConfiguration;

    private KeyPair keyPair;

    @Valid
    public final CacheConfig cacheConfig;

    @Valid
    public Map<LuthenClient, LuthenClientConfig> clientConfigs;

    @Override
    public LuthenClientConfig getLuthenClientConfig(String clientName) {
        return clientConfigs.get(LuthenClient.valueOf(clientName));
    }

    public KeyPair getKeyPair() {
        if(keyPair==null)
            keyPair = CryptoUtils.validateGenerateKeys(authConfig.getPublicKey(), authConfig.getPrivateKey());
        return keyPair;
    }

    @Override
    public ECPublicKey getPublicKey() {
        return (ECPublicKey) getKeyPair().getPublic();
    }

    public ECPrivateKey getPrivateKey() {
        return (ECPrivateKey) getKeyPair().getPrivate();
    }

    @Override
    public String getIssuer() {
        return authConfig.getIssuer();
    }

}

package com.shadow2y.luthen.service;

import com.shadow2y.luthen.service.model.config.IdentityConfig;
import com.shadow2y.luthen.service.model.config.LuthenAuthConfig;
import com.shadow2y.luthen.service.model.config.LuthenClientConfig;
import com.shadow2y.luthen.service.model.config.LuthenClient;
import io.dropwizard.core.Configuration;
import io.dropwizard.db.DataSourceFactory;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import java.util.Map;

@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AppConfig extends Configuration {

    @Valid @NotNull
    public final DataSourceFactory database;

    @Valid @NotNull
    public LuthenAuthConfig authConfig;

    @Valid @NotNull
    public IdentityConfig identityConfig;

    @Valid
    public Map<LuthenClient,LuthenClientConfig> clientConfigs;

    public SwaggerBundleConfiguration swaggerBundleConfiguration;

}

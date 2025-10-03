package com.shadow2y.luthen.service;

import com.shadow2y.luthen.service.model.config.LuthenAuthConfig;
import com.shadow2y.luthen.service.model.config.LuthenClientConfig;
import com.shadow2y.luthen.service.model.config.LuthenClient;
import io.dropwizard.core.Configuration;
import io.dropwizard.db.DataSourceFactory;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
public class AppConfig extends Configuration {

    @Valid @NotNull
    private DataSourceFactory database;

    @Valid @NotNull
    private LuthenAuthConfig authConfig;

    @Valid
    private Map<LuthenClient,LuthenClientConfig> clientConfigs;

    public SwaggerBundleConfiguration swaggerBundleConfiguration;

    public AppConfig() {
    }
}

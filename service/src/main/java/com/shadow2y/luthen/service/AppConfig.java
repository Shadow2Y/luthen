package com.shadow2y.luthen.service;

import io.dropwizard.core.Configuration;
import io.dropwizard.db.DataSourceFactory;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AppConfig extends Configuration {

    @Valid @NotNull
    private DataSourceFactory database;

    public SwaggerBundleConfiguration swaggerBundleConfiguration;

    @NotNull
    private String jwtSecret;

    private int jwtExpiryMinutes; // access token lifetime

}

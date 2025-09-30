package com.shadow2y.luthen.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.core.Configuration;
import io.dropwizard.db.DataSourceFactory;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AppConfig extends Configuration {

    @Valid
    @NotNull
    private DataSourceFactory database;

    public SwaggerBundleConfiguration swaggerBundleConfiguration;

    @NotNull
    private String jwtSecret;

    private int jwtExpiryMinutes; // access token lifetime

    @NotEmpty
    @JsonProperty("rsaPrivateKey")
    private String rsaPrivateKey;

    @NotEmpty
    @JsonProperty("rsaPublicKey")
    private String rsaPublicKey;

    public AppConfig() {
    }
}

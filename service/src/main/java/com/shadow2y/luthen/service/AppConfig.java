package com.shadow2y.luthen.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.core.Configuration;
import io.dropwizard.db.DataSourceFactory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AppConfig extends Configuration {

    @Valid @NotNull
    private DataSourceFactory database;

    @NotNull
    private String jwtSecret;

    private int jwtExpiryMinutes = 60; // access token lifetime

}

package com.shadow2y.luthen.service.model.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ValkeyConfig {

    @Valid
    public String host;

    @Valid
    public int port;

    public String password;

    @Valid
    public int timeout;

    @Valid
    public final int database;

    @Valid
    public final int expiryInSeconds;

}


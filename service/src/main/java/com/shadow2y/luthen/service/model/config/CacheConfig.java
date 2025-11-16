package com.shadow2y.luthen.service.model.config;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public class CacheConfig {

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
    public final long lookupTtl;

    @Valid
    public final long defaultExpirySeconds;

    public final Map<String, Long> keyToTtlInSeconds;

}


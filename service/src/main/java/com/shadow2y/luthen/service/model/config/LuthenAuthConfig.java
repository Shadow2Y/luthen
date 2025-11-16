package com.shadow2y.luthen.service.model.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class LuthenAuthConfig {

    private int jwtExpiryMinutes;

    @Valid @NotNull
    private int passwordSaltRounds;

    @Valid @NotEmpty
    private String issuer;

    @NotEmpty
    private String privateKey;

    @NotEmpty
    private String publicKey;

    @Valid @NotNull
    private String filterAlgorithm;

    @Valid @NotNull
    private String filterSeed;

    @Valid @NotNull
    private int filteringWindowMins;

}

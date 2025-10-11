package com.shadow2y.luthen.service.model.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LuthenAuthConfig {

    private int jwtExpiryMinutes; /// TODO Implementation

    @Valid @NotNull
    private int passwordSaltRounds;

    @NotEmpty
    private String rsaPrivateKey;

    @NotEmpty
    private String rsaPublicKey;

    @Valid @NotNull
    private String filterAlgorithm;

    @Valid @NotNull
    private String filterSeed;

    @Valid @NotNull
    private int filteringWindowMins;

}

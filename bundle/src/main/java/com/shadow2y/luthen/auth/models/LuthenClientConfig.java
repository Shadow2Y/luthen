package com.shadow2y.luthen.auth.models;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LuthenClientConfig {

    @Valid @NotNull
    private String id;

    @Valid @NotNull
    private String secret;

    @Valid
    private int refreshInMins;

}

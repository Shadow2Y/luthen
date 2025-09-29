package com.shadow2y.luthen.api.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Set;

@Data
public class SignupRequest {

    @Valid @NotNull
    String username;

    @Valid @NotNull
    String email;

    @Valid @NotNull
    String password;

    Set<String> roles;

}

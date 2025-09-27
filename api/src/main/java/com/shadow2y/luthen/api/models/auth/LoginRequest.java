package com.shadow2y.luthen.api.models.auth;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoginRequest {

    String username;

    String email;

    @Valid @NotNull
    String password;

}

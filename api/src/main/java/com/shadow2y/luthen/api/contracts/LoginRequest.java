package com.shadow2y.luthen.api.contracts;

public record LoginRequest(
        String username,
        String email,
        String password
) {
}

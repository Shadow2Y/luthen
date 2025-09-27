package com.shadow2y.luthen.api.models.auth;

import lombok.Data;

@Data
public class LoginResponse {
    long createdAt;
    long expiresAt;
    String accessToken;
    String refreshToken;
}

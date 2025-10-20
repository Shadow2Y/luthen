package com.shadow2y.luthen.api.contracts;

public record LoginResponse(
        long createdAt,
        long expiresAt,
        String accessToken,
        String refreshToken
) {
}

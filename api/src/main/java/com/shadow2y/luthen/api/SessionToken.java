package com.shadow2y.luthen.api;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Objects;

@Data
public class SessionToken {
    private String tokenId;
    private String userId;
    private String token;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private boolean isActive;

    public SessionToken() {}

    public SessionToken(String userId, String token, LocalDateTime expiresAt) {
        this.userId = userId;
        this.token = token;
        this.createdAt = LocalDateTime.now();
        this.expiresAt = expiresAt;
        this.isActive = true;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SessionToken that = (SessionToken) o;
        return Objects.equals(tokenId, that.tokenId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tokenId);
    }
}

package com.shadow2y.luthen.service.service;

import com.shadow2y.luthen.service.service.intf.TokenService;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public class TokenServiceImpl implements TokenService {

    private final SessionStore sessionStore;

    public TokenServiceImpl(SessionStore sessionStore) {
        this.sessionStore = sessionStore;
    }

    @Override
    public String generateToken() {
        return String.format("%s-%s",UUID.randomUUID(), System.currentTimeMillis());
    }

    @Override
    public SessionToken createAccessToken(long userId, int durationMinutes) {
        String token = generateToken();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(durationMinutes);

        SessionToken sessionToken = new SessionToken(userId, token, expiresAt);

        return sessionStore.save(sessionToken);
    }

    @Override
    public boolean verifyAccessToken(String token) {
        Optional<SessionToken> sessionOpt = sessionStore.findByToken(token);
        return sessionOpt.isPresent() &&
                sessionOpt.get().isActive() &&
                !sessionOpt.get().isExpired();
    }

    @Override
    public void invalidateToken(String token) {
        sessionStore.deleteByToken(token);
    }

    @Override
    public void invalidateAllUserTokens(String userId) {
        sessionStore.deleteByUserId(userId);
    }

    @Override
    public void cleanupExpiredTokens() {
        sessionStore.deleteExpiredTokens();
    }
}

package com.shadow2y.luthen.service.service;

import com.shadow2y.luthen.api.SessionToken;
import com.shadow2y.luthen.service.repository.intf.SessionStore;
import com.shadow2y.luthen.service.service.intf.TokenService;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public class TokenServiceImpl implements TokenService {
    private final SessionStore tokenRepository;

    public TokenServiceImpl(SessionStore tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Override
    public String generateToken() {
        return String.format("%s-%s",UUID.randomUUID(), System.currentTimeMillis());
    }

    @Override
    public SessionToken createSessionToken(String userId, int durationMinutes) {
        String token = generateToken();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(durationMinutes);

        SessionToken sessionToken = new SessionToken(userId, token, expiresAt);
        sessionToken.setTokenId(UUID.randomUUID().toString());

        return tokenRepository.save(sessionToken);
    }

    @Override
    public boolean validateToken(String token) {
        Optional<SessionToken> sessionOpt = tokenRepository.findByToken(token);
        return sessionOpt.isPresent() &&
                sessionOpt.get().isActive() &&
                !sessionOpt.get().isExpired();
    }

    @Override
    public void invalidateToken(String token) {
        tokenRepository.deleteByToken(token);
    }

    @Override
    public void invalidateAllUserTokens(String userId) {
        tokenRepository.deleteByUserId(userId);
    }

    @Override
    public void cleanupExpiredTokens() {
        tokenRepository.deleteExpiredTokens();
    }
}

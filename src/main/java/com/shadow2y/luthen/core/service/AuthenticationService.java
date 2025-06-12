package com.shadow2y.luthen.core.service;

import com.shadow2y.luthen.core.exception.AuthenticationException;
import com.shadow2y.luthen.core.exception.UserAlreadyExistsException;
import com.shadow2y.luthen.core.exception.UserNotFoundException;
import com.shadow2y.luthen.core.model.SessionToken;
import com.shadow2y.luthen.core.model.User;
import com.shadow2y.luthen.core.repository.SessionTokenRepository;
import com.shadow2y.luthen.core.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

public class AuthenticationService {
    private final UserRepository userRepository;
    private final SessionTokenRepository tokenRepository;
    private final PasswordService passwordService;
    private final TokenService tokenService;

    public AuthenticationService(
            UserRepository userRepository,
            SessionTokenRepository tokenRepository,
            PasswordService passwordService,
            TokenService tokenService) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordService = passwordService;
        this.tokenService = tokenService;
    }

    public User signUp(String username, String email, String password) {
        // Check if user already exists
        if (userRepository.existsByUsername(username)) {
            throw new UserAlreadyExistsException("Username already exists: " + username);
        }

        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException("Email already exists: " + email);
        }

        // Create new user
        String hashedPassword = passwordService.hashPassword(password);
        User user = new User(username, email, hashedPassword);
        user.setUserId(UUID.randomUUID().toString());

        return userRepository.save(user);
    }

    public SessionToken login(String usernameOrEmail, String password) {
        // Find user by username or email
        Optional<User> userOpt = userRepository.findByUsername(usernameOrEmail);
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByEmail(usernameOrEmail);
        }

        if (userOpt.isEmpty()) {
            throw new AuthenticationException("Invalid credentials");
        }

        User user = userOpt.get();

        // Verify password
        if (!passwordService.verifyPassword(password, user.getPasswordHash())) {
            throw new AuthenticationException("Invalid credentials");
        }

        if (!user.isActive()) {
            throw new AuthenticationException("Account is deactivated");
        }

        // Create session token
        return tokenService.createSessionToken(user.getUserId(), 720); // 12 hours
    }

    public void logout(String token) {
        tokenService.invalidateToken(token);
    }

    public void logoutAllSessions(String userId) {
        tokenService.invalidateAllUserTokens(userId);
    }

    public boolean validateSession(String token) {
        return tokenService.validateToken(token);
    }

    public Optional<User> getCurrentUser(String token) {
        Optional<SessionToken> sessionOpt = tokenRepository.findByToken(token);
        if (sessionOpt.isEmpty() || !sessionOpt.get().isActive() || sessionOpt.get().isExpired()) {
            return Optional.empty();
        }

        return userRepository.findById(sessionOpt.get().getUserId());
    }

    public void changePassword(String userId, String currentPassword, String newPassword) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new UserNotFoundException("User not found");
        }

        User user = userOpt.get();

        // Verify current password
        if (!passwordService.verifyPassword(currentPassword, user.getPasswordHash())) {
            throw new AuthenticationException("Current password is incorrect");
        }

        // Update password
        String hashedNewPassword = passwordService.hashPassword(newPassword);
        user.setPasswordHash(hashedNewPassword);
        userRepository.save(user);

        // Invalidate all existing sessions for security
        tokenService.invalidateAllUserTokens(userId);
    }
}

package com.shadow2y.luthen.service.service;

import com.shadow2y.luthen.api.models.auth.LoginRequest;
import com.shadow2y.luthen.api.models.auth.UserAuth;
import com.shadow2y.luthen.service.exception.AuthenticationException;
import com.shadow2y.luthen.service.exception.UserAlreadyExistsException;
import com.shadow2y.luthen.service.model.enums.UserStatus;
import com.shadow2y.luthen.service.repository.stores.UserStore;
import com.shadow2y.luthen.service.repository.tables.User;
import com.shadow2y.luthen.service.service.intf.PasswordService;
import com.shadow2y.luthen.service.service.intf.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

@Slf4j
public class AuthService {
    private final UserStore userStore;
    private final PasswordService passwordService;
    private final TokenService tokenService;

    public AuthService(UserStore userStore, PasswordService passwordService, TokenService tokenService) {
        this.userStore = userStore;
        this.tokenService = tokenService;
        this.passwordService = passwordService;
    }

    public User signUp(String username, String email, String password) {
        // Check if user already exists
        if (userStore.existsByUsername(username)) {
            throw new UserAlreadyExistsException("Username already exists :: " + username);
        }

        if (userStore.existsByEmail(email)) {
            throw new UserAlreadyExistsException("Email already exists :: " + email);
        }

        String hashedPassword = passwordService.hashPassword(password);
        User user = new User(username, email, hashedPassword);

        return userStore.save(user);
    }

    public String login(LoginRequest request) {
        var user = getUser(request.getUsername(), request.getEmail());

        validateEntity(user, request);

        return tokenService.createAccessToken(new UserAuth(LocalDate.now().plusDays(1), user.getUsername(), user.getRoleNames()));
    }

    private void validateEntity(User user, LoginRequest request) {
        if (!passwordService.verifyPassword(request.getPassword(), user.getPassword())) {
            throw new AuthenticationException("Invalid credentials");
        }

        if (!UserStatus.ACTIVE.equals(user.getStatus())) {
            throw new AuthenticationException("Account is deactivated");
        }
    }

    public void logout(String token) {
        tokenService.invalidateToken(token);
    }

    public void logoutAllSessions(String userId) {
        tokenService.invalidateAllUserTokens(userId);
    }

    public boolean validateSession(String token) {
        return tokenService.verifyAccessToken(token);
    }

//    public Optional<User> getUserFromToken(String token) {
//        Optional<SessionToken> sessionOpt = tokenRepository.findByToken(token);
//        if (sessionOpt.isEmpty() || !sessionOpt.get().isActive() || sessionOpt.get().isExpired()) {
//            return Optional.empty();
//        }
//
//        return userStore.findById(sessionOpt.get().getUserId());
//    }

    public void changePassword(String username, String currentPassword, String newPassword) {
        User user = getUser(username,null);

        // Verify current password
        if (!passwordService.verifyPassword(currentPassword, user.getPassword())) {
            throw new AuthenticationException("Current password is incorrect");
        }

        // Update password
        String hashedNewPassword = passwordService.hashPassword(newPassword);
        user.setPassword(hashedNewPassword);
        userStore.save(user);

        // Invalidate all existing sessions for security
        tokenService.invalidateAllUserTokens(username);
    }


    public User getUser(String username, String email) {
        Optional<User> user = Optional.empty();
        if(!StringUtils.isEmpty(username)) {
            user = userStore.findByUsername(username);
        } else if(!StringUtils.isEmpty(email)) {
            user = userStore.findByEmail(email);
        }
        if(user.isEmpty()) {
            throw new AuthenticationException("Username or Password is invalid");
        }
        return user.get();
    }

}

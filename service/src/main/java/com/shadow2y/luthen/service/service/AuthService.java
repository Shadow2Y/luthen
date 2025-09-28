package com.shadow2y.luthen.service.service;

import com.shadow2y.luthen.api.models.auth.LoginRequest;
import com.shadow2y.luthen.api.models.auth.SignupRequest;
import com.shadow2y.luthen.api.models.auth.UserAuth;
import com.shadow2y.luthen.service.exception.AuthenticationException;
import com.shadow2y.luthen.service.exception.Error;
import com.shadow2y.luthen.service.exception.LuthenError;
import com.shadow2y.luthen.service.model.enums.UserStatus;
import com.shadow2y.luthen.service.repository.stores.UserStore;
import com.shadow2y.luthen.service.repository.tables.User;
import com.shadow2y.luthen.service.service.intf.PasswordService;
import com.shadow2y.luthen.service.service.intf.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
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

    public User signUp(SignupRequest signupRequest) throws LuthenError {
        String username = signupRequest.getUsername();
        String email = signupRequest.getEmail();
        String password = signupRequest.getPassword();

        validateNewUser(username, email);

        String hashedPassword = passwordService.hashPassword(password);
        User user = new User(username, email, hashedPassword);

        return userStore.save(user);
    }

    public String login(LoginRequest request) throws LuthenError {
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

    private void validateNewUser(String username, String email) throws LuthenError {
        if (userStore.existsByUsername(username)) {
            throw new LuthenError(Error.USERNAME_ALREADY_EXISTS);
        }

        if (userStore.existsByEmail(email)) {
            throw new LuthenError(Error.EMAIL_ALREADY_EXISTS);
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

    public void changePassword(String username, String currentPassword, String newPassword) throws LuthenError {
        User user = getUser(username,null);

        // Verify current password
        if (!passwordService.verifyPassword(currentPassword, user.getPassword())) {
            throw new LuthenError(Error.INCORRECT_OLD_PASSWORD);
        }

        // Update password
        String hashedNewPassword = passwordService.hashPassword(newPassword);
        user.setPassword(hashedNewPassword);
        userStore.save(user);

        // Invalidate all existing sessions for security
        tokenService.invalidateAllUserTokens(username);
    }


    public User getUser(String username, String email) throws LuthenError {
        Optional<User> user = Optional.empty();
        if(!StringUtils.isEmpty(username)) {
            user = userStore.findByUsername(username);
        } else if(!StringUtils.isEmpty(email)) {
            user = userStore.findByEmail(email);
        }
        if(user.isEmpty()) {
            throw new LuthenError(Error.INVALID_CREDENTIALS);
        }
        return user.get();
    }

}

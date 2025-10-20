package com.shadow2y.luthen.service.service;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.shadow2y.luthen.api.contracts.CreateRoleRequest;
import com.shadow2y.luthen.api.contracts.LoginRequest;
import com.shadow2y.luthen.api.contracts.LoginResponse;
import com.shadow2y.luthen.api.summary.RoleSummary;
import com.shadow2y.luthen.api.summary.UserSummary;
import com.shadow2y.luthen.service.exception.Error;
import com.shadow2y.luthen.service.exception.LuthenError;
import com.shadow2y.luthen.api.models.UserStatus;
import com.shadow2y.luthen.service.repository.stores.PermissionStore;
import com.shadow2y.luthen.service.repository.stores.RoleStore;
import com.shadow2y.luthen.service.repository.stores.UserStore;
import com.shadow2y.luthen.service.repository.tables.User;
import com.shadow2y.luthen.service.service.intf.PasswordService;
import com.shadow2y.luthen.service.service.intf.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

@Slf4j
public class AuthService {

    private final TokenService tokenService;
    private final PasswordService passwordService;
    private final UserStore userStore;
    private final RoleStore roleStore;
    private final PermissionStore permissionStore;

    public AuthService(TokenService tokenService, PasswordService passwordService, UserStore userStore, RoleStore roleStore, PermissionStore permissionStore) {
        this.roleStore = roleStore;
        this.userStore = userStore;
        this.permissionStore = permissionStore;
        this.tokenService = tokenService;
        this.passwordService = passwordService;
    }

    public User validateAndAddRoles(User user, List<String> roleNames) throws LuthenError {
        if (roleNames == null || roleNames.isEmpty()) {
            return user;
        }
        var roles = roleStore.getRoles(roleNames);
        if (roles.size() != roleNames.size()) {
            throw new LuthenError(Error.ROLE_NOT_FOUND);
        }
        user.getRoles().addAll(roles);
        user = userStore.save(user);
        return user;
    }

    public String getOrCreatePermission(String permissionName, String description) throws LuthenError {
        var permissionOpt = permissionStore.getOrCreatePermission(permissionName, description);
        if(permissionOpt.isPresent()) {
            return permissionOpt.get().getName();
        }
        throw new LuthenError(Error.INTERNAL_DATABASE_ERROR);
    }

    public UserSummary updateUser(UserSummary request) throws LuthenError {
        User user = getUser(request.username(), request.email());
        updateUser(user, request.roles());
        user = userStore.save(user);
        return user.toSummary();
    }

    public RoleSummary getOrCreateRole(CreateRoleRequest request) throws LuthenError {
        var role = roleStore.updateOrCreateRole(request.name(), request.description(), request.permissions());
        log.info("Executed getOrCreateRole for role: {}", role.getName());
        return role.getSummary();
    }

    public LoginResponse login(LoginRequest request) throws LuthenError {
        var user = getUser(request.username(), request.email());

        validateEntity(user, request);
        var signedJWT = tokenService.createAccessToken(user.toSummary());
        var jwt = getJwt(signedJWT);
        return new LoginResponse(
                jwt.getIssueTime().getTime(),
                jwt.getExpirationTime().getTime(),
                signedJWT.serialize(),
                ""
        );
    }

    private JWTClaimsSet getJwt(SignedJWT signedJWT) throws LuthenError {
        try {
            return signedJWT.getJWTClaimsSet();
        } catch (ParseException e) {
            throw new LuthenError(Error.INTERNAL_SERVER_ERROR, e);
        }
    }

    private void validateEntity(User user, LoginRequest request) throws LuthenError {
        if (!passwordService.verifyPassword(request.password(), user.getPassword())) {
            throw new LuthenError(Error.INVALID_CREDENTIALS);
        }
        if (!UserStatus.ACTIVE.equals(user.getStatus())) {
            throw new LuthenError(Error.ACCOUNT_DEACTIVATED);
        }

        log.debug("Validated user with username :: {}", user.getUsername());
    }

    public Map<String,Object> introspect(String token) throws LuthenError {
        return tokenService.validateGetClaims(token).get().toJSONObject();
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
        User user = null;
        if(!StringUtils.isEmpty(email)) {
            user = userStore.findByEmail(email).orElse(null);
        } else if(!StringUtils.isEmpty(username)) {
            user = userStore.findByUsername(username).orElse(null);
        }
        if(user==null) {
            throw new LuthenError(Error.INVALID_USER_OR_CREDENTIALS);
        }
        log.info("Fetched user :: {} from DB", username);
        return user;
    }

    public void updateUser(User user, List<String> roleNames) throws LuthenError {
        try {
            var roles = roleStore.getRoles(roleNames);
            user.setRoles(roles);
        } catch (Exception e) {
            throw new LuthenError(Error.INTERNAL_DATABASE_ERROR, e);
        }
    }

}

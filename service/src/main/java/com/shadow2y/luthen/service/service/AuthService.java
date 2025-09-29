package com.shadow2y.luthen.service.service;

import com.shadow2y.luthen.api.request.CreateRoleRequest;
import com.shadow2y.luthen.api.request.LoginRequest;
import com.shadow2y.luthen.api.response.LoginResponse;
import com.shadow2y.luthen.api.request.SignupRequest;
import com.shadow2y.luthen.api.response.UserAuth;
import com.shadow2y.luthen.api.summary.PermissionSummary;
import com.shadow2y.luthen.api.summary.RoleSummary;
import com.shadow2y.luthen.api.summary.UserSummary;
import com.shadow2y.luthen.service.exception.Error;
import com.shadow2y.luthen.service.exception.LuthenError;
import com.shadow2y.luthen.api.models.UserStatus;
import com.shadow2y.luthen.service.repository.stores.PermissionStore;
import com.shadow2y.luthen.service.repository.stores.RoleStore;
import com.shadow2y.luthen.service.repository.stores.UserStore;
import com.shadow2y.luthen.service.repository.tables.Permission;
import com.shadow2y.luthen.service.repository.tables.Role;
import com.shadow2y.luthen.service.repository.tables.User;
import com.shadow2y.luthen.service.service.intf.PasswordService;
import com.shadow2y.luthen.service.service.intf.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.Set;

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

    public UserSummary signUp(SignupRequest signupRequest) throws LuthenError {
        String username = signupRequest.getUsername();
        String email = signupRequest.getEmail();
        String password = signupRequest.getPassword();

        validateNewUser(username, email);

        String hashedPassword = passwordService.hashPassword(password);
        User user = new User(username, email, hashedPassword);

        log.info("User registered successfully: {}", username);
        user = userStore.save(user);
        return user.toSummary();
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

    public Role validateAndAddPermissions(Role role, List<String> permissionList) throws LuthenError {
        if(permissionList == null || permissionList.isEmpty()) {
            return role;
        }
        var permissions = validateGetPermissions(permissionList);
        role.getPermissions().addAll(permissions);
        role = roleStore.save(role);
        return role;
    }

    public Set<Permission> validateGetPermissions(List<String> permissions) throws LuthenError {
        var permissionsList = permissionStore.getPermissions(permissions);
        if(permissions.size() != permissionsList.size()) {
            throw new LuthenError(Error.PERMISSION_NOT_FOUND);
        }
        return permissionsList;
    }

    public PermissionSummary getOrCreatePermission(String permissionName, String description) {
        return permissionStore.getOrCreatePermission(permissionName, description).toSummary();
    }

    public RoleSummary getOrCreateRole(CreateRoleRequest request) throws LuthenError {
        var role = roleStore.updateOrCreateRole(request.name, request.description, validateGetPermissions(request.permissions));
        log.info("Executed getOrCreateRole for role: {}", role.getName());
        return role.toSummary();
    }

    public LoginResponse login(LoginRequest request) throws LuthenError {
        var user = getUser(request.getUsername(), request.getEmail());

        validateEntity(user, request);
        var userAuth = tokenService.createAccessToken(new UserAuth(user.getUsername(), user.getRoleNames()));
        return new LoginResponse()
                .setAccessToken(userAuth.getAccessToken())
                .setCreatedAt(userAuth.getCreatedAt().toEpochMilli())
                .setExpiresAt(userAuth.getExpiresAt().toEpochMilli())
                ;
    }

    private void validateEntity(User user, LoginRequest request) throws LuthenError {
        if (!passwordService.verifyPassword(request.getPassword(), user.getPassword())) {
            throw new LuthenError(Error.INVALID_CREDENTIALS);
        }

        if (!UserStatus.ACTIVE.equals(user.getStatus())) {
            throw new LuthenError(Error.ACCOUNT_DEACTIVATED);
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
            throw new LuthenError(Error.INVALID_OR_USER_CREDENTIALS);
        }
        return user.get();
    }

}

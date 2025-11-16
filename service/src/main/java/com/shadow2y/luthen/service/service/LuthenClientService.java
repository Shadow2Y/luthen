package com.shadow2y.luthen.service.service;

import com.shadow2y.luthen.api.summary.RoleSummary;
import com.shadow2y.luthen.service.AppConfig;
import com.shadow2y.luthen.service.exception.Error;
import com.shadow2y.luthen.service.exception.LuthenError;
import com.shadow2y.luthen.service.model.config.LuthenClient;
import com.shadow2y.luthen.auth.models.LuthenClientConfig;
import com.shadow2y.luthen.api.contracts.ClientRefreshRequest;
import com.shadow2y.luthen.api.contracts.ClientRefreshResponse;
import com.shadow2y.luthen.service.repository.stores.CacheStore;
import com.shadow2y.luthen.service.repository.stores.PermissionStore;
import com.shadow2y.luthen.service.repository.stores.RoleStore;
import com.shadow2y.luthen.service.repository.tables.Role;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.experimental.UtilityClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Singleton
public class LuthenClientService {

    AppConfig appConfig;
    CacheService cacheService;

    private final Logger log = LoggerFactory.getLogger(LuthenClientService.class);

    @Inject
    public LuthenClientService(AppConfig appConfig, CacheService cacheService) {
        this.appConfig = appConfig;
        this.cacheService = cacheService;
        ClientFilter.init(appConfig.authConfig.getFilterAlgorithm(), appConfig.authConfig.getFilterSeed(), appConfig.authConfig.getFilteringWindowMins());
    }

    public ClientRefreshResponse getAuthData(String filterKey, ClientRefreshRequest request) throws LuthenError {
        validateFilterKey(filterKey);
        var clientConfig = getClientConfig(request.getClientName());
        validateLuthenClient(request.getClientId(), request.getClientSecret(), clientConfig.getId(), clientConfig.getSecret());
        var roles = cacheService.fetchClientLookup();
//        var blacklists = cacheService.
        return toRefreshResponse(getExpiryTime(clientConfig), List.of(), roles);
    }

    private void validateFilterKey(String filterKey) throws LuthenError {
        if(!ClientFilter.verifyFilterKey(filterKey)) {
            log.error("Client Filter verification failed :: Received filterKey :: {}", filterKey);
            log.debug("Expected filterKey :: {}", ClientFilter.generate());
            throw new LuthenError(Error.CLIENT_FILTER_VALIDATION_FAILED);
        }
    }

    private ClientRefreshResponse toRefreshResponse(Instant expiryTime, List<String> invalidatedTokens, List<RoleSummary> roles) {
        var response = new ClientRefreshResponse();
        response.setExpiryTime(expiryTime);
        response.setBlacklistedUsers(new HashSet<>(invalidatedTokens));
        response.setRoleList(roles);
        return response;
    }

    private Map<String, Set<String>> getRolesAsMap(List<Role> roles) {
        return roles.stream().collect(
                Collectors.toMap(
                    Role::getName,
                    Role::getPermissions
                ));
    }

    private void validateLuthenClient(String clientId, String clientSecret, String expectedClientId, String expectedClientSecret) throws LuthenError {
        boolean validationFailed = false;
        if(!Objects.requireNonNull(clientId).equals(expectedClientId)) {
            log.error("ClientId is invalid, actual :: {} expected :: {}", clientId, expectedClientId);
            validationFailed = true;
        }
        if(!Objects.requireNonNull(clientSecret).equals(expectedClientSecret)) {
            log.error("ClientSecret is invalid, actual :: {} expected :: {}", clientSecret, expectedClientSecret);
            validationFailed = true;
        }
        if (validationFailed)
            throw new LuthenError(Error.CLIENT_VALIDATION_FAILED);
    }

    public LuthenClientConfig getClientConfig(String clientName) throws LuthenError {
        try {
            LuthenClient client = LuthenClient.valueOf(clientName);
            return appConfig.clientConfigs.get(client);
        } catch (Throwable e) {
            log.error("Client Validation Failed, ERROR :: ",e);
            throw new LuthenError(Error.CLIENT_VALIDATION_FAILED);
        }
    }

    private Instant getExpiryTime(LuthenClientConfig clientConfig) {
        return Instant.now().plusSeconds(60L * clientConfig.getRefreshInMins()).truncatedTo(ChronoUnit.MILLIS);
    }

    @UtilityClass
    private static final class ClientFilter {

        private static String seed;
        private static long windowMins;
        private static MessageDigest digest;
        private static long lastWindow;
        private static String lastGeneratedKey;
        private static final Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();

        void init(String algorithm, String seed, long windowMins) {
            if(seed!=null && digest!=null) return;
            ClientFilter.seed = seed;
            ClientFilter.windowMins = windowMins;
            try {
                digest = MessageDigest.getInstance(algorithm);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }

        synchronized String generate() {
            long currentWindow = Instant.now().getEpochSecond() / (60 * windowMins);

            // Reuse previously generated key if same window
            if (currentWindow == lastWindow && lastGeneratedKey != null) {
                return lastGeneratedKey;
            }

            String input = seed + currentWindow;
            lastGeneratedKey = hashString(input);
            lastWindow = currentWindow;

            return lastGeneratedKey;
        }

        String hashString(String input) {
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return encoder.encodeToString(hash).substring(0, 32);
        }

        boolean verifyFilterKey(String candidate) {
            return candidate.equals(generate());
        }
    }

}

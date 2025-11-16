package com.shadow2y.luthen.service.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.shadow2y.commons.Result;
import com.shadow2y.luthen.api.summary.RoleSummary;
import com.shadow2y.luthen.service.AppConfig;
import com.shadow2y.luthen.service.model.config.CacheConfig;
import com.shadow2y.luthen.service.repository.stores.CacheStore;
import com.shadow2y.luthen.service.repository.stores.RoleStore;
import com.shadow2y.luthen.service.repository.tables.Role;
import com.shadow2y.luthen.service.utils.LuthenUtils;
import com.shadow2y.luthen.service.utils.SerDe;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Singleton
public class CacheService {

    final long lookupTtl;
    final long otpExpiry;
    final CacheConfig config;
    final RoleStore roleStore;
    final CacheStore cacheStore;
    final String lookupKey = "lookup:";
    final String blacklistKey = "blacklist:";
    final String otpKeyPrefix = "otp:email:";

    @Inject
    public CacheService(AppConfig appConfig, RoleStore roleStore, CacheStore cacheStore) {
        this.roleStore = roleStore;
        this.cacheStore = cacheStore;
        this.config = appConfig.cacheConfig;
        this.lookupTtl = appConfig.cacheConfig.lookupTtl;
        this.otpExpiry = appConfig.identityConfig.otpExpirySeconds;
    }

    public void gracefulRefreshClientLookup() {
        if(cacheStore.getExpiryOfKey(lookupKey) < LuthenUtils.getEpochSecond()) {
            refreshClientLookup();
        }
    }

    public List<RoleSummary> refreshClientLookup() {
        List<RoleSummary> summary = roleStore.getAllRoles().stream().map(Role::getSummary).toList();
        cacheStore.saveData(lookupKey, lookupTtl, SerDe.writeValue(summary));
        return summary;
    }

    public List<RoleSummary> fetchClientLookup() {
        var cachedLookup = cacheStore.getData(lookupKey);
        if(cachedLookup.isPresent()) {
            return SerDe.readValue(cachedLookup.get(), new TypeReference<>() {});
        } else {
            return refreshClientLookup();
        }
    }

    public void blacklistUser(String userName) {
        cacheStore.saveData(blacklistKey + userName, "");
    }

    public void saveOtp(String emailId, String otp) {
        saveData(otpKeyPrefix+emailId, otp, otpExpiry);
    }

    public Result<String,?> getOtp(String emailId) {
        return getData(otpKeyPrefix + emailId, new TypeReference<>() {});
    }

    public void saveData(String key, String value, long ttlInSeconds) {
        log.info("Trying to save data for key :: {}, with ttl :: {}", key, ttlInSeconds);
        cacheStore.saveData(key, ttlInSeconds, value);
    }

    public <T> Result<T, Exception> getData(String key, TypeReference<T> type) {
        log.info("Trying to fetch data for key :: {}, for type :: {}", key, type);
        return cacheStore.getData(key, type);
    }

}

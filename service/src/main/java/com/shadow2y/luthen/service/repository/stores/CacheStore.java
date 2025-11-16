package com.shadow2y.luthen.service.repository.stores;

import com.fasterxml.jackson.core.type.TypeReference;
import com.shadow2y.commons.Result;
import com.shadow2y.luthen.service.AppConfig;
import com.shadow2y.luthen.service.repository.common.ValkeyFactory;
import com.shadow2y.luthen.service.utils.LuthenUtils;
import com.shadow2y.luthen.service.utils.SerDe;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

public class CacheStore {

    private static final Logger log = LoggerFactory.getLogger(CacheStore.class);

    final JedisPool jedisPool;
    final long defaultExpiry;
    private final Map<String, Long> KeytoTtlMap;

    @Inject
    public CacheStore(AppConfig appConfig) {
        var config = appConfig.cacheConfig;
        this.jedisPool = ValkeyFactory.build(config);
        this.defaultExpiry = config.defaultExpirySeconds;
        this.KeytoTtlMap = config.keyToTtlInSeconds;
    }

    public void saveData(String key, long ttlInSeconds, Object data) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.setex(key, ttlInSeconds, SerDe.writeValue(data));
        }
    }

    public void saveData(String key, String data) {
        saveData(key, getDefaultTtlOfKey(key), data);
    }

    public <T> Result<T, Exception> getData(String key, TypeReference<T> type) {
        try (Jedis jedis = jedisPool.getResource()) {
            var result = jedis.get(key);
            if(LuthenUtils.isEmpty(result))
                return Result.empty();
            return Result.of(
                    SerDe.readValue(result, type)
            );
        } catch (Exception e) {
            log.error("Error occurred while operation :getData: {}",e.getMessage());
            return Result.error(e);
        }
    }

    public Result<String, Exception> getData(String key) {
        return getData(key, new TypeReference<>() {});
    }

    public long getExpiryOfKey(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            var ttl =  jedis.ttl(key);
            return LuthenUtils.getEpochSecond() + ttl;
        }
    }

    public long getDefaultTtlOfKey(String key) {
        return KeytoTtlMap.getOrDefault(key, defaultExpiry);
    }

}

package com.shadow2y.luthen.service.repository.common;

import com.shadow2y.luthen.service.model.config.CacheConfig;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class ValkeyFactory {

    public static JedisPool build(CacheConfig config) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        return new JedisPool(
                poolConfig,
                config.host,
                config.port,
                config.timeout,
                config.password,
                config.database
        );
    }

}


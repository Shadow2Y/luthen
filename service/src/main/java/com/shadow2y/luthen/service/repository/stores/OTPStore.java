package com.shadow2y.luthen.service.repository.stores;

import com.shadow2y.commons.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class OTPStore {

    private static final Logger log = LoggerFactory.getLogger(OTPStore.class);

    final int expiryInSeconds;
    final JedisPool jedisPool;
    private final String OtpKey = "otp:email:";

    public OTPStore(JedisPool jedisPool, int expiryInSeconds) {
        this.jedisPool = jedisPool;
        this.expiryInSeconds = expiryInSeconds;
    }

    public void save(String emailId, String otp) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.setex(OtpKey + emailId, expiryInSeconds, otp);
        }
    }

    public Result<String,?> getOtp(String emailId) {
        try (Jedis jedis = jedisPool.getResource()) {
            return Result.of(jedis.get(OtpKey + emailId));
        }
    }

}

package com.shadow2y.luthen.service.repository.stores;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class OTPStore {

    private static final Logger log = LoggerFactory.getLogger(OTPStore.class);

    final JedisPool jedisPool;

    public OTPStore(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public void save(String emailId, String otp) {
        try (Jedis jedis = jedisPool.getResource()) {
            // Store OTP for 5 minutes (300 seconds)
            jedis.setex("otp:email:" + emailId, 300, otp);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getOtp(String emailId) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get("otp:email:" + emailId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}

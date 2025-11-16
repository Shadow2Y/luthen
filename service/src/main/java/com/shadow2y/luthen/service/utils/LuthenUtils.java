package com.shadow2y.luthen.service.utils;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public final class LuthenUtils {

    public static final ZoneId defaultZone = ZoneId.systemDefault();
    public static final SecureRandom RANDOM = new SecureRandom();
    public static final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public static boolean isAfter(LocalDateTime before, LocalDateTime after) {
        return before.atZone(defaultZone).isAfter(after.atZone(defaultZone));
    }

    public static boolean isEmpty(String o) {
        return o==null || o.isEmpty();
    }

    public static String generateRandomString(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(ALPHANUMERIC.charAt(RANDOM.nextInt(ALPHANUMERIC.length())));
        }
        return sb.toString();
    }

    public static String generateRandomDigits(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(RANDOM.nextInt(10));
        }
        return sb.toString();
    }

    public static long getEpochSecond() {
        return Instant.now().getEpochSecond();
    }

}

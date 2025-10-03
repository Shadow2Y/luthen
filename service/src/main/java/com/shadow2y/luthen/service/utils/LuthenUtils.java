package com.shadow2y.luthen.service.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;

public final class LuthenUtils {

    public static final ZoneId defaultZone = ZoneId.systemDefault();

    public static boolean isAfter(LocalDateTime before, LocalDateTime after) {
        return before.atZone(defaultZone).isAfter(after.atZone(defaultZone));
    }

    static boolean isEmpty(String o) {
        return o==null || o.isEmpty();
    }

}

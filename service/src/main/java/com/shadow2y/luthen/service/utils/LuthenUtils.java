package com.shadow2y.luthen.service.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public final class LuthenUtils {

    public static final ZoneId defaultZone = ZoneId.systemDefault();

    public static boolean isAfter(LocalDateTime before, LocalDateTime after) {
        return before.atZone(defaultZone).isAfter(after.atZone(defaultZone));
    }

}

package com.shadow2y.luthen.service.managed;

import com.shadow2y.luthen.service.AppConfig;
import com.shadow2y.luthen.service.service.CacheService;
import io.dropwizard.lifecycle.Managed;
import java.util.concurrent.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CacheManager implements Managed {

    private final CacheService cacheService;

    private static final Logger log = LoggerFactory.getLogger(CacheManager.class);
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public CacheManager(AppConfig appConfig, CacheService cacheService) {
        this.cacheService = cacheService;
    }

    @Override
    public void start() {
        log.info("Starting cache refresh manager...");
        // 1️⃣ Warm up cache on startup
        scheduler.submit(cacheService::gracefulRefreshClientLookup);

        // 2️⃣ Schedule periodic refresh every 10 minutes
        scheduler.scheduleAtFixedRate(cacheService::gracefulRefreshClientLookup, 15, 15, TimeUnit.MINUTES);
    }

    @Override
    public void stop() {
        log.info("Stopping cache refresh manager...");
        scheduler.shutdownNow();
    }
}


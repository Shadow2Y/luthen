package com.shadow2y.luthen.service.general;

import com.shadow2y.luthen.service.model.config.AsyncExecutorConfig;
import io.dropwizard.lifecycle.Managed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;
import java.util.function.Supplier;

public class AsyncExecutorManager implements Managed {

    private final AsyncExecutorConfig config;
    public static ThreadPoolExecutor Executor;

    private static final Logger log = LoggerFactory.getLogger(AsyncExecutorManager.class);

    public AsyncExecutorManager(AsyncExecutorConfig config) {
        this.config = config;
    }

    @Override
    public void start() {
        if (Executor != null && !Executor.isShutdown()) return;

        Executor = new ThreadPoolExecutor(
                config.coreThreads,
                config.maxThreads,
                config.keepAliveSeconds,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(config.queueCapacity),
                new ThreadFactory() {
                    private final ThreadFactory base = Executors.defaultThreadFactory();
                    private int count = 0;

                    @Override
                    public Thread newThread(Runnable r) {
                        Thread t = base.newThread(r);
                        t.setName("async-executor-" + (++count));
                        t.setUncaughtExceptionHandler((thr, ex) ->
                                log.error("Uncaught exception in " + thr.getName(), ex)
                        );
                        return t;
                    }
                },
                new ThreadPoolExecutor.CallerRunsPolicy()
        );

        log.info("AsyncExecutor started: " + config);
    }

    @Override
    public void stop() {
        if (Executor == null) return;
        log.info("Shutting down AsyncExecutor...");
        Executor.shutdown();
        try {
            if (!Executor.awaitTermination(10, TimeUnit.SECONDS)) {
                Executor.shutdownNow();
                log.warn("Forced AsyncExecutor shutdown.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Executor.shutdownNow();
        }
    }

    public static CompletableFuture<Void> runAsync(Runnable task) {
        if (Executor == null) throw new IllegalStateException("Executor not initialized");
        return CompletableFuture.runAsync(task, Executor);
    }

    public static <T> CompletableFuture<T> supplyAsync(Supplier<T> supplier) {
        if (Executor == null) throw new IllegalStateException("Executor not initialized");
        return CompletableFuture.supplyAsync(() -> {
            try {
                return supplier.get();
            } catch (Exception e) {
                log.error("Error executing async supplier", e);
                throw e;
            }
        }, Executor);
    }

    private static Runnable wrap(Runnable task) {
        return () -> {
            try {
                task.run();
            } catch (Exception e) {
                log.error("Error executing async task", e);
            }
        };
    }

}

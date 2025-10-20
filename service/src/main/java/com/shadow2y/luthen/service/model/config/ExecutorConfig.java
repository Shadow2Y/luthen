package com.shadow2y.luthen.service.model.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Min;

public class ExecutorConfig implements com.shadow2y.commons.executor.AsyncExecutorConfig {

    @Min(1) @JsonProperty
    private int coreThreads = Math.max(2, Runtime.getRuntime().availableProcessors());

    @Min(1) @JsonProperty
    private int maxThreads = coreThreads * 2;

    @Min(1) @JsonProperty
    private int queueCapacity = 1000;

    @Min(1) @JsonProperty
    private long keepAliveSeconds = 30;

    @Override
    public int coreThreads() {
        return coreThreads;
    }

    @Override
    public int queueCapacity() {
        return queueCapacity;
    }

    @Override
    public long keepAliveSeconds() {
        return keepAliveSeconds;
    }

    @Override
    public int maxThreads() {
        return maxThreads;
    }
}


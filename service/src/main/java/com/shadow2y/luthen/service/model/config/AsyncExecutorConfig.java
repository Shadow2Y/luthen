package com.shadow2y.luthen.service.model.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Min;

public class AsyncExecutorConfig {

    @Min(1) @JsonProperty
    public int coreThreads = Math.max(2, Runtime.getRuntime().availableProcessors());

    @Min(1) @JsonProperty
    public int maxThreads = coreThreads * 2;

    @Min(1) @JsonProperty
    public int queueCapacity = 1000;

    @Min(1) @JsonProperty
    public long keepAliveSeconds = 30;

}


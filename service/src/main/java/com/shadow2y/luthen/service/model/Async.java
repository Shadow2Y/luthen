package com.shadow2y.luthen.service.model;

import com.shadow2y.luthen.service.general.AsyncExecutorManager;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

public class Async<A> {

    final CompletableFuture<A> async;

    public Async(Supplier<A> async) {
        this.async = AsyncExecutorManager.supplyAsync(async);
    }

    public static <T> CompletableFuture<Result<T,?>> of(Supplier<T> supplier) {
        return AsyncExecutorManager.supplyAsync(() -> {
            try {
                return Result.ok(supplier.get());
            } catch (Exception e) {
                return Result.error(e);
            }
        });
    }

    public static CompletableFuture<Void> of(Runnable async) {
        return AsyncExecutorManager.runAsync(async);
    }

    public A get() throws ExecutionException, InterruptedException {
        return async.get();
    }

    public A getInclusive() {
        try {
            return async.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}

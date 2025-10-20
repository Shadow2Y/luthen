package com.shadow2y.luthen.service.model;

import com.shadow2y.luthen.service.exception.LuthenError;

import java.util.Optional;
import java.util.function.Supplier;

public class Result<R, E extends Throwable> {

    R result;
    E error;

    public static final Result<?,?> Empty = new Result<>(null);


    public Result() {
    }

    public Result(R result) {
        this.result = result;
    }

    public Result(R result, E error) {
        this.result = result;
        this.error = error;
    }

    public static <R,E extends Throwable> Result<R,E> of(R result) {
        return new Result<>(result);
    }

    public static <R> Result<R,?> ok(R result) {
        return new Result<>(result);
    }

    public static <R> Result<R,?> from(Optional<R> result) {
        var obj = result.orElse(null);
        return new Result<>(obj);
    }

    @SuppressWarnings("unchecked")
    public static <R,E extends Throwable> Result<R,E> empty() {
        return (Result<R,E>) Empty;
    }

    public static <R,E extends Throwable> Result<R,E> error(E e) {
        return new Result<>(null, e);
    }

    public boolean isPresent() {
        return result!=null;
    }

    public boolean isEmpty() {
        return result==null;
    }

    public Result<R,E> set(R result) {
        this.result = result;
        return this;
    }

    public Result<R,E> setError(E e) {
        this.error = e;
        return this;
    }

    public R get() {
        return result;
    }

    public R getOrDefault(R fallback) {
        return orElse(fallback);
    }

    public R orElse(R fallback) {
        if(result!=null) {
            return result;
        } else {
            return fallback;
        }
    }

    public <T extends Exception> R elseThrow(Supplier<T> e) throws T {
        if(result!=null) {
            return result;
        } else {
            throw e.get();
        }
    }

    public Result<R,E> throwIfError() throws E {
        if(error !=null) {
            throw error;
        } else {
            return this;
        }
    }

}

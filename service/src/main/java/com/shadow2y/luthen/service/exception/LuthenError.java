package com.shadow2y.luthen.service.exception;

public class LuthenError extends Throwable {

    public final Error error;

    public LuthenError(Error error) {
        super(error.getReason());
        this.error = error;
    }

    public LuthenError(Error error, Throwable cause) {
        super(error.getReason(), cause);
        this.error = error;
    }

}

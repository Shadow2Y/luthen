package com.shadow2y.luthen.service.exception;

public class LuthenError extends Exception {

    public final Error error;

    public LuthenError(Error error) {
        super(error.getReason());
        this.error = error;
    }

    public LuthenError(Error error, Throwable cause) {
        super(error.getReason(), cause);
        this.error = error;
    }

    public LuthenError(Error error, String detailedMessage) {
        super(error.getReason() +"Details :: \n"+detailedMessage);
        this.error = error;
    }

    public LuthenError(Error error, String detailedMessage, Throwable cause) {
        super(error.getReason() +"Details :: \n"+detailedMessage, cause);
        this.error = error;
    }

}

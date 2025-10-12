package com.shadow2y.luthen.service.exception;

import io.dropwizard.jersey.errors.ErrorMessage;
import lombok.Getter;

public enum Error {

    OK(200, "OK"),
    CREATED(201, "Created"),
    ACCEPTED(202, "Accepted"),
    BAD_REQUEST(400, "Bad Request"),
    INVALID_CREDENTIALS(400, "Invalid Credentials"),
    INVALID_USER_OR_CREDENTIALS(400, "Invalid User or Credentials"),
    INCORRECT_OLD_PASSWORD(400, "Current password is incorrect"),
    USERNAME_ALREADY_EXISTS(400, "Username already exists"),
    EMAIL_ALREADY_EXISTS(400, "Email already exists"),
    UNAUTHORIZED(401, "Unauthorized"),
    ACCOUNT_DEACTIVATED(401, "Account is deactivated"),
    INVALID_TOKEN_SIGNATURE(401, "Invalid Token Signature"),
    TOKEN_EXPIRED(401, "Token has expired"),
    INVALID_TOKEN(401, "Invalid Token"),
    INVALID_REFRESH_TOKEN(401, "Invalid Refresh Token"),
    PAYMENT_REQUIRED(402, "Payment Required"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"),
    ROLE_NOT_FOUND(405, "Invalid Role"),
    PERMISSION_NOT_FOUND(405, "Invalid Permission"),
    TOO_MANY_REQUESTS(429, "Too Many Requests"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    INTERNAL_DATABASE_ERROR(500, "Internal Database Error"),
    TOKEN_AUTHENTICATION_FAILED(500, "Token Authentication Failed"),
    CLIENT_VALIDATION_FAILED(500, "Client Validation Failed"),
    CLIENT_FILTER_VALIDATION_FAILED(500, "Client Filter Validation Failed"),
    SIGNUP_MAIL_FAILED(500, "Error occurred while attempting Signup flow"),
    OTP_VALIDATION_FAILED(500, "Unable to validate OTP"),
    ;

    @Getter
    private final ErrorMessage errorMessage;

    Error(int statusCode, String reason) {
        this.errorMessage = new ErrorMessage(statusCode, reason);
    }

    public int getStatusCode() {
        return this.errorMessage.getCode();
    }

    public String getReason() {
        return this.errorMessage.getMessage();
    }

}
package com.shadow2y.luthen.service.exception;

import io.dropwizard.jersey.errors.ErrorMessage;
import lombok.Getter;

public enum Error {

    OK(200, "OK"),
    CREATED(201, "Created"),
    ACCEPTED(202, "Accepted"),
    NO_CONTENT(204, "No Content"),
    RESET_CONTENT(205, "Reset Content"),
    PARTIAL_CONTENT(206, "Partial Content"),
    MOVED_PERMANENTLY(301, "Moved Permanently"),
    FOUND(302, "Found"),
    SEE_OTHER(303, "See Other"),
    NOT_MODIFIED(304, "Not Modified"),
    USE_PROXY(305, "Use Proxy"),
    TEMPORARY_REDIRECT(307, "Temporary Redirect"),
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
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    NOT_ACCEPTABLE(406, "Not Acceptable"),
    PROXY_AUTHENTICATION_REQUIRED(407, "Proxy Authentication Required"),
    REQUEST_TIMEOUT(408, "Request Timeout"),
    CONFLICT(409, "Conflict"),
    GONE(410, "Gone"),
    LENGTH_REQUIRED(411, "Length Required"),
    PRECONDITION_FAILED(412, "Precondition Failed"),
    REQUEST_ENTITY_TOO_LARGE(413, "Request Entity Too Large"),
    REQUEST_URI_TOO_LONG(414, "Request-URI Too Long"),
    UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"),
    REQUESTED_RANGE_NOT_SATISFIABLE(416, "Requested Range Not Satisfiable"),
    EXPECTATION_FAILED(417, "Expectation Failed"),
    PRECONDITION_REQUIRED(428, "Precondition Required"),
    TOO_MANY_REQUESTS(429, "Too Many Requests"),
    REQUEST_HEADER_FIELDS_TOO_LARGE(431, "Request Header Fields Too Large"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    INTERNAL_DATABASE_ERROR(500, "Internal Database Error"),
    TOKEN_AUTHENTICATION_FAILED(500, "Token Authentication Failed"),
    NOT_IMPLEMENTED(501, "Not Implemented"),
    BAD_GATEWAY(502, "Bad Gateway"),
    SERVICE_UNAVAILABLE(503, "Service Unavailable"),
    GATEWAY_TIMEOUT(504, "Gateway Timeout"),
    HTTP_VERSION_NOT_SUPPORTED(505, "HTTP Version Not Supported"),
    NETWORK_AUTHENTICATION_REQUIRED(511, "Network Authentication Required");

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
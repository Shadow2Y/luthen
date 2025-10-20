package com.shadow2y.luthen.api.contracts;

public record SignupVerifyRequest(String username, String email, String password, String otp) {
}

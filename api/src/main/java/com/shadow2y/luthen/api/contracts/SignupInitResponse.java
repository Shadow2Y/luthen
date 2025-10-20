package com.shadow2y.luthen.api.contracts;

import java.time.Instant;

public record SignupInitResponse(
        Instant otpExpiry,
        String email
) {
}

package com.shadow2y.luthen.api.response;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class LoginResponse {
    long createdAt;
    long expiresAt;
    String accessToken;
    String refreshToken;
}

package com.shadow2y.luthen.api.models;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.security.auth.Subject;
import java.security.Principal;
import java.time.Instant;
import java.util.List;

@Data
@Accessors(chain = true)
public class UserAuth implements Principal {

    private final String username;

    private String accessToken;

    private Instant createdAt;

    private Instant expiresAt;

    private List<String> roles;

    public UserAuth(String username, List<String> roles) {
        this.username = username;
        this.roles = roles;
    }

    @Override
    public String getName() {
        return this.username;
    }

    @Override
    public boolean implies(Subject subject) {
        return Principal.super.implies(subject);
    }

}


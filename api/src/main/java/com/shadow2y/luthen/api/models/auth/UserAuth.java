package com.shadow2y.luthen.api.models.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import javax.security.auth.Subject;
import java.security.Principal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
public class UserAuth implements Principal {

    private LocalDate expiry;

    private String username;

    private List<String> roles;

    @Override
    public String getName() {
        return this.username;
    }

    @Override
    public boolean implies(Subject subject) {
        return Principal.super.implies(subject);
    }
}


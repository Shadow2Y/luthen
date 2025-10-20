package com.shadow2y.luthen.api.contracts;

import java.util.Set;

public record CreateRoleRequest(
        String name,
        String description,
        Set<String> permissions
) {
}

package com.shadow2y.luthen.api.contracts;

import java.util.List;

public record CreateRoleRequest(
        String name,
        String description,
        List<String> permissions
) {
}

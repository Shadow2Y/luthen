package com.shadow2y.luthen.api.summary;

import java.util.Set;

public record RoleSummary(
        String name,
        String description,
        Set<String> permissions
) {}

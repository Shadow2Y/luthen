package com.shadow2y.luthen.api.summary;

import java.util.List;

public record RoleSummary(String name, String description, List<PermissionSummary> permissions) {
}

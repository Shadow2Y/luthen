package com.shadow2y.luthen.api.summary;

import com.shadow2y.luthen.api.models.UserStatus;

import java.time.LocalDateTime;
import java.util.List;

public record UserSummary(String username, String email, UserStatus status, List<String> roles, LocalDateTime createdAt) {
}

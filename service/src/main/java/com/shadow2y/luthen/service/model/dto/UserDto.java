package com.shadow2y.luthen.service.model.dto;

import com.shadow2y.luthen.service.model.enums.UserStatus;

import java.time.LocalDateTime;

public record UserDto(
        Long id,
        String username,
        String email,
        UserStatus status,
        LocalDateTime createdDate
) {}

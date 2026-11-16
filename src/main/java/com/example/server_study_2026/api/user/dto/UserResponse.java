package com.example.server_study_2026.api.user.dto;

import com.example.server_study_2026.domain.user.User;

import java.time.LocalDateTime;

public record UserResponse(Long id, String email, LocalDateTime createdAt) {
    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getCreatedAt());
    }
}

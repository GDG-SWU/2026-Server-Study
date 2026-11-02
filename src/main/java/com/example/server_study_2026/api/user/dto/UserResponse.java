package com.example.server_study_2026.api.user.dto;

import com.example.server_study_2026.domain.user.Role;
import com.example.server_study_2026.domain.user.User;

public record UserResponse(Long id, String email, Role role) {
    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getRole());
    }
}

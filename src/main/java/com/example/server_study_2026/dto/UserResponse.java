package com.example.server_study_2026.dto;

import com.example.server_study_2026.domain.User;
import lombok.Getter;

@Getter
public class UserResponse {
    private Long id;
    private String email;
    private String role;

    public UserResponse(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.role = user.getRole().name();
    }
}
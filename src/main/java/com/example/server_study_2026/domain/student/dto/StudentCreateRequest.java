package com.example.server_study_2026.domain.student.dto;

public record StudentCreateRequest(
        String name,
        String password
) {
}

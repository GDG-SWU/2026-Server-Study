package com.example.server_study_2026.api.user.dto;

public record TokenResponse(String tokenType, String accessToken) {
    public static TokenResponse of(String accessToken) {
        return new TokenResponse("Bearer", accessToken);
    }
}

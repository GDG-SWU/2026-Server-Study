package com.example.server_study_2026.api.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "LoginResponse")
public record TokenResponse(String accessToken) {}

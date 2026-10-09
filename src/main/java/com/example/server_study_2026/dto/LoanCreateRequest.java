package com.example.server_study_2026.dto;

public record LoanCreateRequest(
        Long userId,
        Long bookId
) {}
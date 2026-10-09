package com.example.server_study_2026.dto;

public record BookCreateRequest(
        String title,
        String author
) {}
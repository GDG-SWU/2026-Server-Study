package com.example.server_study_2026.domain.book.dto;

public record BookCreateRequest (
        String title,
        String author
) {}

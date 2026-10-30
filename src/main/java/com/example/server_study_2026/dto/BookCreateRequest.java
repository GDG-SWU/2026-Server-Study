package com.example.server_study_2026.dto;

import jakarta.validation.constraints.NotBlank;

public record BookCreateRequest(
        @NotBlank(message = "도서 제목은 필수입니다.")
        String title,

        @NotBlank(message = "저자명은 필수입니다.")
        String author
) {}
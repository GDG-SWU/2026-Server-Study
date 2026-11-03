package com.example.server_study_2026.domain.book.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BookCreateRequest (

        @NotBlank(message = "제목은 필수입니다.")
        @Size(max = 100, message = "제목은 100자 이하입니다.")
                String title,

        @NotBlank(message = "저자는 필수입니다.")
        String author
) {}

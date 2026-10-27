package com.example.server_study_2026.api.book.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

//도서 생성 Request Dto
public record BookCreateRequest(
        @NotBlank(message = "도서 제목은 필수입니다.")
        @Schema(description = "도서 제목", example = "객체지향의 사실과 오해")
        String title,

        @NotBlank(message = "저자는 필수입니다.")
        @Schema(description = "저자", example = "조영호")
        String author
) {}

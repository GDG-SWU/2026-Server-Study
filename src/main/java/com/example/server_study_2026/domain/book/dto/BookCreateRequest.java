package com.example.server_study_2026.domain.book.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BookCreateRequest {

    @NotBlank(message = "도서 제목은 필수입니다.")
    @Size(max = 100, message = "도서 제목은 100자 이하여야 합니다.")
    private String title;

    private String author;
}
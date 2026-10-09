package com.gdg.library.book;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

public record BookCreateRequest(
        @NotBlank @Size(max = 255) @Schema(example = "자바의 정석") String title,
        @NotBlank @Size(max = 255) @Schema(example = "남궁성") String author
) {}

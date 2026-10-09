package com.gdg.library.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

public record UserCreateRequest(
        @NotBlank @Email @Size(max = 100) @Schema(example = "sua@example.com") String email,
        @NotBlank @Size(max = 255)
        @Schema(example = "practice-password", accessMode = Schema.AccessMode.WRITE_ONLY) String password
) {}

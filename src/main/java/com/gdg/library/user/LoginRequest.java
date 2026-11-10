package com.gdg.library.user;

import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;

public record LoginRequest(
        @NotBlank @Email @Size(max = 100) String email,
        @NotBlank @Size(max = 255) @Schema(accessMode = Schema.AccessMode.WRITE_ONLY) String password
) {}

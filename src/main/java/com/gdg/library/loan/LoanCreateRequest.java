package com.gdg.library.loan;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import io.swagger.v3.oas.annotations.media.Schema;

public record LoanCreateRequest(
        @NotNull @Positive @Schema(example = "1") Long userId,
        @NotNull @Positive @Schema(example = "1") Long bookId
) {}

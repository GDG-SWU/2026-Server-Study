package com.gdg.library.loan;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import io.swagger.v3.oas.annotations.media.Schema;

public record LoanCreateRequest(
        @NotNull(message = "도서 ID는 필수입니다.") @Positive(message = "도서 ID는 양수여야 합니다.")
        @Schema(example = "1") Long bookId
) {}

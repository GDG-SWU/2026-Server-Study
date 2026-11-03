package com.example.server_study_2026.domain.loan.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record LoanCreateRequest(
        @NotNull(message = "회원 ID는 필수입니다.")
        @Positive(message = "회원 ID는 양수여야 합니다.")
        Long studentId,                                   // ✅ 추가 — 대출하는 사람

        @NotNull(message = "도서 ID는 필수입니다.")
        @Positive(message = "도서 ID는 양수여야 합니다.")
        Long bookId                                       // 대출할 책
) {}
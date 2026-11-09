package com.example.server_study_2026.domain.loan.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoanCreateRequest {

    @NotNull(message = "회원 ID는 필수입니다.")
    private Long userId;

    @NotNull(message = "도서 ID는 필수입니다.")
    private Long bookId;
}
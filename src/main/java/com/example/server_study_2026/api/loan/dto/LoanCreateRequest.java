package com.example.server_study_2026.api.loan.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

//대출 신청(생성) Request Dto
public record LoanCreateRequest (
        @NotNull(message = "회원 ID는 필수입니다.")
        @Schema(description = "대출하는 회원의 ID", example = "1")
        Long userId,

        @NotNull(message = "도서 ID는 필수입니다.")
        @Schema(description = "대출할 도서의 ID", example = "1")
        Long bookId
){}

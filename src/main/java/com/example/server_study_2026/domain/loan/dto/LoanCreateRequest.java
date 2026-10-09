package com.example.server_study_2026.domain.loan.dto;

public record LoanCreateRequest(
        Long studentId,
        Long bookId

) {
}

package com.example.server_study_2026.domain.loan.dto;

import com.example.server_study_2026.domain.loan.Loan;

import java.time.LocalDate;

public record LoanResponse(
        Long id,
        Long studentId,
        Long book,
        LocalDate loanDate,
        LocalDate dueDate,
        LocalDate returnDate


) {
    public static LoanResponse from(Loan loan) {
        return new LoanResponse(
                loan.getId(),
                loan.getStudent().getId(),
                loan.getBook().getId(),
                loan.getLoanDate(),
                loan.getDueDate(),
                loan.getReturnDate()
        );
    }
}

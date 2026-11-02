package com.example.server_study_2026.domain.loan.dto;

import com.example.server_study_2026.domain.loan.Loan;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class LoanResponse {

    private final Long id;
    private final Long userId;
    private final Long bookId;
    private final LocalDate loanDate;
    private final LocalDate dueDate;
    private final LocalDate returnDate;

    public LoanResponse(Loan loan) {
        this.id = loan.getId();
        this.userId = loan.getUser().getId();
        this.bookId = loan.getBook().getId();
        this.loanDate = loan.getLoanDate();
        this.dueDate = loan.getDueDate();
        this.returnDate = loan.getReturnDate();
    }


}

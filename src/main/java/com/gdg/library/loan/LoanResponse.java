package com.gdg.library.loan;

import java.time.LocalDate;

public record LoanResponse(Long id, Long userId, Long bookId,
                           LocalDate loanDate, LocalDate dueDate, LocalDate returnDate) {
    public static LoanResponse from(Loan loan) {
        return new LoanResponse(loan.getId(), loan.getUser().getId(), loan.getBook().getId(),
                loan.getLoanDate(), loan.getDueDate(), loan.getReturnDate());
    }
}

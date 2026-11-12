
package com.example.server_study_2026.domain.loan;

import com.example.server_study_2026.domain.book.Book;
import com.example.server_study_2026.domain.book.BookRepository;
import com.example.server_study_2026.domain.loan.dto.LoanCreateRequest;
import com.example.server_study_2026.domain.loan.dto.LoanResponse;
import com.example.server_study_2026.domain.user.User;
import com.example.server_study_2026.domain.user.UserRepository;
import com.example.server_study_2026.global.exception.BusinessException;
import com.example.server_study_2026.global.exception.ErrorCode;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class LoanService {

    private final LoanRepository loanRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public LoanService(
            LoanRepository loanRepository,
            UserRepository userRepository,
            BookRepository bookRepository
    ) {
        this.loanRepository = loanRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    // 대출 신청
    public LoanResponse createLoan(LoanCreateRequest request, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() ->
                        new BusinessException(ErrorCode.BOOK_NOT_FOUND));

        if (Boolean.TRUE.equals(book.getIsBorrowed())) {
            throw new BusinessException(ErrorCode.BOOK_ALREADY_LOANED);
        }

        book.borrow();

        Loan loan = Loan.builder()
                .user(user)
                .book(book)
                .loanDate(LocalDate.now())
                .build();

        Loan savedLoan = loanRepository.save(loan);

        return new LoanResponse(savedLoan);
    }

    // 도서 반납
    public LoanResponse returnLoan(Long id, Long userId) {

        Loan loan = loanRepository.findById(id)
                .orElseThrow(() ->
                        new BusinessException(ErrorCode.LOAN_NOT_FOUND));

        checkLoanOwner(loan, userId);

        if (loan.getReturnDate() != null) {
            throw new BusinessException(ErrorCode.LOAN_ALREADY_RETURNED);
        }

        loan.completeReturn(LocalDate.now());

        return new LoanResponse(loan);
    }

    // 대출 목록 조회
    public List<LoanResponse> getLoans(Long userId) {

        return loanRepository.findAll()
                .stream()
                .filter(loan -> loan.getUser().getId().equals(userId))
                .map(LoanResponse::new)
                .toList();
    }

    // 대출 단건 조회
    public LoanResponse getLoan(Long id, Long userId) {

        Loan loan = loanRepository.findById(id)
                .orElseThrow(() ->
                        new BusinessException(ErrorCode.LOAN_NOT_FOUND));

        checkLoanOwner(loan, userId);

        return new LoanResponse(loan);
    }

    // 대출 기록 삭제
    public void deleteLoan(Long id, Long userId) {

        Loan loan = loanRepository.findById(id)
                .orElseThrow(() ->
                        new BusinessException(ErrorCode.LOAN_NOT_FOUND));

        checkLoanOwner(loan, userId);

        if (loan.getReturnDate() == null) {
            loan.completeReturn(LocalDate.now());
        }

        loanRepository.delete(loan);
    }

    // 대출 기록 소유자 확인
    private void checkLoanOwner(Loan loan, Long userId) {

        if (!loan.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
    }
}

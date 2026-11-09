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
    public LoanResponse createLoan(LoanCreateRequest request) {
        User user = userRepository.findById(request.getUserId())
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
    public LoanResponse returnLoan(Long id) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() ->
                        new BusinessException(ErrorCode.LOAN_NOT_FOUND));

        if (loan.getReturnDate() != null) {
            throw new BusinessException(ErrorCode.LOAN_ALREADY_RETURNED);
        }

        loan.completeReturn(LocalDate.now());

        return new LoanResponse(loan);
    }

    // 대출 목록 조회
    public List<LoanResponse> getLoans() {
        return loanRepository.findAll()
                .stream()
                .map(LoanResponse::new)
                .toList();
    }

    // 대출 단건 조회
    public LoanResponse getLoan(Long id) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() ->
                        new BusinessException(ErrorCode.LOAN_NOT_FOUND));

        return new LoanResponse(loan);
    }

    // 대출 기록 삭제
    public void deleteLoan(Long id) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() ->
                        new BusinessException(ErrorCode.LOAN_NOT_FOUND));

        if (loan.getReturnDate() == null) {
            loan.completeReturn(LocalDate.now());
        }

        loanRepository.delete(loan);
    }
}
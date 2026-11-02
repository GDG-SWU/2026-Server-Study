package com.example.server_study_2026.domain.loan;

import com.example.server_study_2026.domain.book.Book;
import com.example.server_study_2026.domain.book.BookRepository;
import com.example.server_study_2026.domain.loan.dto.LoanCreateRequest;
import com.example.server_study_2026.domain.loan.dto.LoanResponse;
import com.example.server_study_2026.domain.user.User;
import com.example.server_study_2026.domain.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

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

    public LoanResponse createLoan(LoanCreateRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new IllegalArgumentException("도서를 찾을 수 없습니다."));

        if (Boolean.TRUE.equals(book.getIsBorrowed())) {
            throw new IllegalStateException("이미 대출 중인 도서입니다.");
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

    public LoanResponse returnLoan(Long id) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("대출 기록을 찾을 수 없습니다."));

        if (loan.getReturnDate() != null) {
            throw new IllegalStateException("이미 반납 처리된 대출입니다.");
        }

        loan.completeReturn(LocalDate.now());

        return new LoanResponse(loan);
    }
    // 대출 목록 조회
    @Transactional
    public java.util.List<LoanResponse> getLoans() {
        return loanRepository.findAll()
                .stream()
                .map(LoanResponse::new)
                .toList();
    }

    // 대출 단건 조회
    @Transactional
    public LoanResponse getLoan(Long id) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("대출 기록을 찾을 수 없습니다."));

        return new LoanResponse(loan);
    }

    // 대출 기록 삭제
    public void deleteLoan(Long id) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("대출 기록을 찾을 수 없습니다."));

        if (loan.getReturnDate() == null) {
            loan.completeReturn(java.time.LocalDate.now());
        }

        loanRepository.delete(loan);
    }

}

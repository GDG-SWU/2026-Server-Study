package com.example.server_study_2026.service;

import com.example.server_study_2026.domain.Book;
import com.example.server_study_2026.domain.Loan;
import com.example.server_study_2026.domain.User;
import com.example.server_study_2026.dto.LoanCreateRequest;
import com.example.server_study_2026.dto.LoanResponse;
import com.example.server_study_2026.global.exception.BusinessException;
import com.example.server_study_2026.global.exception.ErrorCode;
import com.example.server_study_2026.repository.BookRepository;
import com.example.server_study_2026.repository.LoanRepository;
import com.example.server_study_2026.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoanService {

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    /**
     * 도서 대출 신청 (토큰의 이메일 활용)
     */
    @Transactional
    public LoanResponse createLoan(String email, LoanCreateRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new BusinessException(ErrorCode.BOOK_NOT_FOUND));

        if (book.isBorrowed()) {
            throw new BusinessException(ErrorCode.ALREADY_BORROWED);
        }

        book.borrow();
        Loan loan = Loan.builder()
                .user(user)
                .book(book)
                .loanDate(LocalDate.now())
                .build();

        Loan saved = loanRepository.save(loan);
        return LoanResponse.from(saved);
    }

    /**
     * 도서 반납 처리 (남의 대출 반납 불가 검증 포함)
     */
    @Transactional
    public LoanResponse returnLoan(String email, Long id) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("대출 내역이 없습니다. id=" + id));

        // 과제 조건: 남의 대출은 반납 불가 (ADMIN이거나 본인의 대출인지 확인)
        if (user.getRole() != User.Role.ADMIN && !loan.getUser().getId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        if (loan.getReturnDate() != null) {
            throw new IllegalStateException("이미 반납된 도서입니다. id=" + id);
        }

        loan.completeReturn(LocalDate.now());
        return LoanResponse.from(loan);
    }

    /**
     * 특정 회원의 대출 내역 조회
     */
    public List<LoanResponse> findByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("회원이 없습니다. id=" + userId);
        }
        return loanRepository.findByUserId(userId).stream()
                .map(LoanResponse::from)
                .toList();
    }

    /**
     * 현재 대출 중인 도서 목록 조회
     */
    public List<LoanResponse> findActiveLoans() {
        return loanRepository.findByReturnDateIsNull().stream()
                .map(LoanResponse::from)
                .toList();
    }
}
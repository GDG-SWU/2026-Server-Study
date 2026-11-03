package com.example.server_study_2026.service;

import com.example.server_study_2026.domain.book.Book;
import com.example.server_study_2026.domain.book.BookRepository;
import com.example.server_study_2026.domain.loan.Loan;
import com.example.server_study_2026.domain.loan.LoanRepository;
import com.example.server_study_2026.domain.loan.dto.LoanCreateRequest;
import com.example.server_study_2026.domain.loan.dto.LoanResponse;
import com.example.server_study_2026.domain.student.Student;
import com.example.server_study_2026.domain.student.StudentRepository;
import com.example.server_study_2026.global.exception.BusinessException;   // ✅ 추가
import com.example.server_study_2026.global.exception.ErrorCode;           // ✅ 추가
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
    private final StudentRepository studentRepository;
    private final BookRepository bookRepository;

    // 도서 대출 신청
    @Transactional
    public LoanResponse create(LoanCreateRequest request) {
        Student student = studentRepository.findById(request.studentId())
                .orElseThrow(() -> new BusinessException(ErrorCode.STUDENT_NOT_FOUND));

        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new BusinessException(ErrorCode.BOOK_NOT_FOUND));

        if (book.isBorrowed()) {
            throw new BusinessException(ErrorCode.BOOK_ALREADY_LOANED);
        }

        Loan loan = Loan.builder()
                .student(student)
                .book(book)
                .loanDate(LocalDate.now())
                .build();

        Loan saved = loanRepository.save(loan);
        book.borrow();

        return LoanResponse.from(saved);
    }

    // 특정 회원의 대출 내역 조회
    public List<LoanResponse> findByUserId(Long userId) {
        if (!studentRepository.existsById(userId)) {
            throw new BusinessException(ErrorCode.STUDENT_NOT_FOUND);
        }

        return loanRepository.findByStudentId(userId).stream()
                .map(LoanResponse::from)
                .toList();
    }

    // 도서 반납 처리
    @Transactional
    public LoanResponse returnBook(Long id) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.LOAN_NOT_FOUND));

        if (loan.getReturnDate() != null) {
            throw new BusinessException(ErrorCode.LOAN_ALREADY_RETURNED);
        }

        loan.completeReturn(LocalDate.now());

        return LoanResponse.from(loan);
    }
}
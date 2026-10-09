package com.example.server_study_2026.service.loan;

import com.example.server_study_2026.api.loan.dto.LoanCreateRequest;
import com.example.server_study_2026.api.loan.dto.LoanResponse;
import com.example.server_study_2026.domain.book.Book;
import com.example.server_study_2026.domain.book.BookRepository;
import com.example.server_study_2026.domain.loan.Loan;
import com.example.server_study_2026.domain.loan.LoanRepository;
import com.example.server_study_2026.domain.user.User;
import com.example.server_study_2026.domain.user.UserRepository;
import com.example.server_study_2026.global.exception.BusinessException;
import com.example.server_study_2026.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor //final 필드 생성자 자동생성
@Transactional(readOnly = true)
public class LoanService {

    private final LoanRepository loanRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    //도서 대출 신청
    @Transactional
    public LoanResponse create(LoanCreateRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new BusinessException(ErrorCode.BOOK_NOT_FOUND));

        //if문은 괄호 안이 true일 때만 실행
        if (book.getIsBorrowed()) {
            throw new BusinessException(ErrorCode.BOOK_ALREADY_BORROWED);
        }

        Loan loan = Loan.builder()
                .user(user)
                .book(book)
                .loanDate(LocalDate.now())
                .build();

        Loan saved = loanRepository.save(loan);
        book.borrow();

        return LoanResponse.from(saved);
    }

    //특정 회원의 대출 내역 조회
    public List<LoanResponse> findByUserId(Long userId) {
        if(!userRepository.existsById(userId)) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        return loanRepository.findByUserId(userId).stream()
                .map(LoanResponse::from)
                .toList();
    }

    //도서 반납 처리
    @Transactional
    public LoanResponse returnBook(Long id) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.LOAN_NOT_FOUND));

        if(loan.getReturnDate() != null) {
            throw new BusinessException(ErrorCode.LOAN_ALREADY_RETURNED);
        }

        loan.completeReturn(LocalDate.now());
        return LoanResponse.from(loan);
    }
}

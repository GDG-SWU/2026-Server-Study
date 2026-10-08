package com.example.server_study_2026.service.loan;

import com.example.server_study_2026.api.loan.dto.LoanCreateRequest;
import com.example.server_study_2026.api.loan.dto.LoanResponse;
import com.example.server_study_2026.domain.book.Book;
import com.example.server_study_2026.domain.book.BookRepository;
import com.example.server_study_2026.domain.loan.Loan;
import com.example.server_study_2026.domain.loan.LoanRepository;
import com.example.server_study_2026.domain.user.User;
import com.example.server_study_2026.domain.user.UserRepository;
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
                .orElseThrow(() -> new IllegalArgumentException("회원이 없습니다. id=" + request.userId()));

        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new IllegalArgumentException("도서가 없습니다. id=" + request.bookId()));

        //if문은 괄호 안이 true일 때만 실행
        if (book.getIsBorrowed()) {
            throw new IllegalStateException("이미 대출 중인 도서입니다. id=" + request.bookId());
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
            throw new IllegalArgumentException("회원이 없습니다. id=" + userId);
        }

        return loanRepository.findByUserId(userId).stream()
                .map(LoanResponse::from)
                .toList();
    }

    //도서 반납 처리
    @Transactional
    public LoanResponse returnBook(Long id) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("대출 기록이 없습니다. id=" + id));

        if(loan.getReturnDate() != null) {
            throw new IllegalStateException("이미 반납된 대출입니다. id=" + id);
        }

        loan.completeReturn(LocalDate.now());

        return LoanResponse.from(loan);
    }
}

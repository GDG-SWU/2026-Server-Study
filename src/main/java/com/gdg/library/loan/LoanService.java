package com.gdg.library.loan;

import com.gdg.library.book.*;
import com.gdg.library.common.ApiException;
import com.gdg.library.user.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoanService {
    private final LoanRepository loanRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final Clock clock;

    public List<LoanResponse> findAll() {
        return loanRepository.findAll().stream().map(LoanResponse::from).toList();
    }

    public LoanResponse findById(Long id) {
        return LoanResponse.from(findLoan(id));
    }

    @Transactional
    public LoanResponse create(LoanCreateRequest request) {
        Book book = bookRepository.findByIdForUpdate(request.bookId())
                .orElseThrow(() -> ApiException.notFound("도서", request.bookId()));
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> ApiException.notFound("회원", request.userId()));
        if (book.isBorrowed()) {
            throw ApiException.conflict("이미 대출 중인 도서입니다. bookId=" + book.getId());
        }

        Loan loan = Loan.builder().user(user).book(book).loanDate(LocalDate.now(clock)).build();
        book.borrow();
        // 조회한 Book은 영속 상태: 트랜잭션 종료 시 변경 감지로 함께 UPDATE된다.
        return LoanResponse.from(loanRepository.save(loan));
    }

    @Transactional
    public LoanResponse returnLoan(Long id) {
        Book book = lockBookForLoan(id);
        Loan loan = findLoan(id);
        if (loan.getReturnDate() != null) {
            throw ApiException.conflict("이미 반납한 대출입니다. id=" + id);
        }
        loan.returnLoan(LocalDate.now(clock));
        book.returnBook();
        return LoanResponse.from(loan);
    }

    @Transactional
    public void delete(Long id) {
        Book book = lockBookForLoan(id);
        Loan loan = findLoan(id);
        // 과제의 삭제는 대출 취소도 허용한다. 반납 완료된 옛 기록을 지울 때는
        // 같은 책의 새로운 대출 상태를 건드리지 않는다.
        if (loan.getReturnDate() == null) {
            book.returnBook();
        }
        loanRepository.delete(loan);
    }

    private Loan findLoan(Long id) {
        return loanRepository.findById(id).orElseThrow(() -> ApiException.notFound("대출", id));
    }

    private Book lockBookForLoan(Long id) {
        Long bookId = loanRepository.findBookIdById(id)
                .orElseThrow(() -> ApiException.notFound("대출", id));
        return bookRepository.findByIdForUpdate(bookId)
                .orElseThrow(() -> ApiException.notFound("도서", bookId));
    }
}

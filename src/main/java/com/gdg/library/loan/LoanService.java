package com.gdg.library.loan;

import com.gdg.library.book.*;
import com.gdg.library.common.BusinessException;
import com.gdg.library.common.ErrorCode;
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

    public List<LoanResponse> findByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        return loanRepository.findByUserId(userId).stream().map(LoanResponse::from).toList();
    }

    public LoanResponse findById(Long id) {
        return LoanResponse.from(findLoan(id));
    }

    @Transactional
    public LoanResponse create(LoanCreateRequest request, Long userId) {
        Book book = bookRepository.findByIdForUpdate(request.bookId())
                .orElseThrow(() -> new BusinessException(ErrorCode.BOOK_NOT_FOUND));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        if (book.isBorrowed()) {
            throw new BusinessException(ErrorCode.BOOK_ALREADY_BORROWED);
        }

        Loan loan = Loan.builder().user(user).book(book).loanDate(LocalDate.now(clock)).build();
        book.borrow();
        // 조회한 Book은 영속 상태: 트랜잭션 종료 시 변경 감지로 함께 UPDATE된다.
        return LoanResponse.from(loanRepository.save(loan));
    }

    @Transactional
    public LoanResponse returnLoan(Long id, Long userId) {
        Book book = lockBookForLoan(id);
        Loan loan = findLoan(id);
        if (!loan.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        if (loan.getReturnDate() != null) {
            throw new BusinessException(ErrorCode.LOAN_ALREADY_RETURNED);
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
        return loanRepository.findById(id).orElseThrow(() -> new BusinessException(ErrorCode.LOAN_NOT_FOUND));
    }

    private Book lockBookForLoan(Long id) {
        Long bookId = loanRepository.findBookIdById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.LOAN_NOT_FOUND));
        return bookRepository.findByIdForUpdate(bookId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOOK_NOT_FOUND));
    }
}

package com.gdg.library.loan;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    boolean existsByUserId(Long userId);
    boolean existsByBookId(Long bookId);

    // Loan 엔티티를 먼저 읽으면 락을 기다리는 동안 상태가 오래될 수 있다.
    // 책 ID만 읽고 책 락을 획득한 뒤 Loan을 읽는다.
    @Query("select l.book.id from Loan l where l.id = :id")
    Optional<Long> findBookIdById(@Param("id") Long id);
}

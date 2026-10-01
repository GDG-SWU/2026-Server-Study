package com.example.server_study_2026.domain.loan;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    // 현재 대출 중인 목록 조회(ReturnDate가 null = 반납이 안된거!)
    List<Loan> findByReturnDateIsNull();
    // 특정 유저의 대출 이력 조회
    List<Loan> findByUserId(Long userId);
}

package com.example.server_study_2026.domain.loan;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    // 특정 유저의 대출 이력 조회
    List<Loan> findByUserId(Long userId);
}

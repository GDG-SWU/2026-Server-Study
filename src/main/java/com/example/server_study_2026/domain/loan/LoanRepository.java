package com.example.server_study_2026.domain.loan;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long>{
    // 현재 대출 중인 목록 조회 (return_date가 비어있는 데이터)
    List<Loan> findByReturnDateIsNull();

    // 특정 유저의 대출 이력 조회
    List<Loan> findByStudentId(Long userId);
}
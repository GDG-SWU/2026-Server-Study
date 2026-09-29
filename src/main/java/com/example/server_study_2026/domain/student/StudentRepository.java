package com.example.server_study_2026.domain.student;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByEmail(String email);   // 나중에 로그인 기능 만들 때도 필요
}
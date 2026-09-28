package com.example.server_study_2026.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);   // 나중에 로그인 기능 만들 때도 필요
}
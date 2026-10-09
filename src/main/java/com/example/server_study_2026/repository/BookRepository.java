package com.example.server_study_2026.repository;

import com.example.server_study_2026.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByTitleContaining(String keyword);
    List<Book> findByIsBorrowedFalse(); // 대출 가능한 도서만 조회
}
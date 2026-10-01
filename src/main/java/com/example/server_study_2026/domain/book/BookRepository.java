package com.example.server_study_2026.domain.book;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

//JpaRepository : 인터페이스 선언만으로 핵심 CRUD 기능을 자동 제공
public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByTitleContaining(String title);
}

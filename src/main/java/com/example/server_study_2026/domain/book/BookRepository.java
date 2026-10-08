package com.example.server_study_2026.domain.book;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

//JpaRepository : 인터페이스 선언만으로 핵심 CRUD 기능을 자동 제공
public interface BookRepository extends JpaRepository<Book, Long> {
    //제목으로 도서조회
    List<Book> findByTitleContaining(String title);
    //대출 가능한 도서만 조회
    List<Book> findByIsBorrowed(Boolean isBorrowed);
}

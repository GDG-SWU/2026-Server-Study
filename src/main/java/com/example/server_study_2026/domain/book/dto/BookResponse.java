package com.example.server_study_2026.domain.book.dto;

import com.example.server_study_2026.domain.book.Book;
import lombok.Getter;

@Getter
public class BookResponse {

    private Long id;
    private String title;
    private String author;
    private Boolean isBorrowed;

    public BookResponse(Book book) {
        this.id = book.getId();
        this.title = book.getTitle();
        this.author = book.getAuthor();
        this.isBorrowed = book.getIsBorrowed();
    }
}
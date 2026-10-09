package com.example.server_study_2026.service;

import com.example.server_study_2026.domain.Book;
import com.example.server_study_2026.dto.BookCreateRequest;
import com.example.server_study_2026.dto.BookResponse;
import com.example.server_study_2026.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;

    public List<BookResponse> findAll(Boolean isBorrowed) {
        if (isBorrowed != null && !isBorrowed) {
            return bookRepository.findByIsBorrowedFalse().stream()
                    .map(BookResponse::from)
                    .toList();
        }
        return bookRepository.findAll().stream()
                .map(BookResponse::from)
                .toList();
    }

    public BookResponse findById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("도서가 없습니다. id=" + id));
        return BookResponse.from(book);
    }

    @Transactional
    public BookResponse create(BookCreateRequest request) {
        Book book = Book.builder()
                .title(request.title())
                .author(request.author())
                .build();
        Book saved = bookRepository.save(book);
        return BookResponse.from(saved);
    }

    @Transactional
    public void delete(Long id) {
        bookRepository.deleteById(id);
    }
}
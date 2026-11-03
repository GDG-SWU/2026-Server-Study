package com.example.server_study_2026.service;

import com.example.server_study_2026.domain.book.Book;
import com.example.server_study_2026.domain.book.BookRepository;
import com.example.server_study_2026.domain.book.dto.BookCreateRequest;
import com.example.server_study_2026.domain.book.dto.BookResponse;
import com.example.server_study_2026.global.exception.BusinessException;
import com.example.server_study_2026.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {
    private final BookRepository bookRepository;

    public List<BookResponse> findAll() {
        return bookRepository.findAll().stream()
                .map(BookResponse::from)
                .toList();
    }

    public BookResponse findById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOOK_NOT_FOUND));

        if (book.isBorrowed()) {
            throw new BusinessException(ErrorCode.BOOK_ALREADY_LOANED);
        }
        return BookResponse.from(book);
    }

    @Transactional
    public BookResponse create(BookCreateRequest request) {
        Book book = Book.builder()
                .title(request.title())
                .author(request.author())
                .build();

        Book Saved = bookRepository.save(book);
        return BookResponse.from(Saved);
    }

    @Transactional
    public void delete(Long id) {
        bookRepository.deleteById(id);
    }
}

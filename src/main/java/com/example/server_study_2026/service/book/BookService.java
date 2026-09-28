package com.example.server_study_2026.service.book;

import com.example.server_study_2026.api.book.dto.BookCreateRequest;
import com.example.server_study_2026.api.book.dto.BookResponse;
import com.example.server_study_2026.domain.book.Book;
import com.example.server_study_2026.domain.book.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor //final 필드 생성자 자동생성
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
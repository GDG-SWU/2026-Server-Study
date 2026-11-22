package com.example.server_study_2026.domain.book;

import com.example.server_study_2026.domain.book.dto.BookCreateRequest;
import com.example.server_study_2026.domain.book.dto.BookResponse;
import com.example.server_study_2026.global.exception.BusinessException;
import com.example.server_study_2026.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    // 도서 등록
    public BookResponse createBook(BookCreateRequest request) {
        Book book = new Book(
                request.getTitle(),
                request.getAuthor()
        );

        Book savedBook = bookRepository.save(book);

        return new BookResponse(savedBook);
    }

    // 도서 전체 조회
    @Transactional(readOnly = true)
    public List<BookResponse> getBooks() {
        return bookRepository.findAll()
                .stream()
                .map(BookResponse::new)
                .toList();
    }

    // 도서 단건 조회
    @Transactional(readOnly = true)
    public BookResponse getBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() ->
                        new BusinessException(ErrorCode.BOOK_NOT_FOUND));

        return new BookResponse(book);
    }

    // 도서 삭제
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() ->
                        new BusinessException(ErrorCode.BOOK_NOT_FOUND));

        bookRepository.delete(book);
    }
}
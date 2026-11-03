package com.example.server_study_2026.controller;


import com.example.server_study_2026.domain.book.dto.BookCreateRequest;
import com.example.server_study_2026.domain.book.dto.BookResponse;
import com.example.server_study_2026.global.response.ApiResponse;
import com.example.server_study_2026.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping
    public List<BookResponse> findAll() {
        return bookService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookResponse>> getBook(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(bookService.findById(id)));
    }

    // 반환할 데이터가 없는 메서드 (삭제, 반납 등)
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBook(@PathVariable Long id) {
        bookService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @PostMapping
    @ResponseStatus (HttpStatus.CREATED)
    public BookResponse create (@Valid @RequestBody BookCreateRequest request){
        return bookService.create(request);
    }



    @ResponseStatus (HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id){
        bookService.delete(id);
    }
}

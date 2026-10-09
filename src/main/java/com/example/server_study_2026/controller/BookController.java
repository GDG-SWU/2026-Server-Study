package com.example.server_study_2026.controller;

import com.example.server_study_2026.dto.BookCreateRequest;
import com.example.server_study_2026.dto.BookResponse;
import com.example.server_study_2026.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
@Tag(name = "Book API", description = "도서 관련 API")
public class BookController {

    private final BookService bookService;

    @GetMapping
    @Operation(summary = "도서 목록 조회", description = "전체 도서 목록을 조회합니다. isBorrowed=false로 대출 가능한 도서만 조회할 수 있습니다.")
    public List<BookResponse> findAll(@RequestParam(required = false) Boolean isBorrowed) {
        return bookService.findAll(isBorrowed);
    }

    @GetMapping("/{id}")
    @Operation(summary = "도서 단건 조회", description = "특정 도서를 ID로 조회합니다.")
    public BookResponse findById(@PathVariable Long id) {
        return bookService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "도서 등록", description = "새로운 도서를 등록합니다.")
    public BookResponse create(@RequestBody BookCreateRequest request) {
        return bookService.create(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "도서 삭제", description = "특정 도서를 삭제합니다.")
    public void delete(@PathVariable Long id) {
        bookService.delete(id);
    }
}
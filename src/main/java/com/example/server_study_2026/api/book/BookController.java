package com.example.server_study_2026.api.book;

import com.example.server_study_2026.api.book.dto.BookCreateRequest;
import com.example.server_study_2026.api.book.dto.BookResponse;
import com.example.server_study_2026.service.book.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name="도서 API", description = "도서 조회/등록/삭제 관련 API")
@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @Operation(summary="도서 목록 조회", description = "전체 도서를 조회합니다.")
    @GetMapping
    public List<BookResponse> findAll() {
        return bookService.findAll();
    }

    @Operation(summary="도서 단권 조회", description = "해당 아이디의 도서를 조회합니다.")
    @GetMapping("/{id}")
    public BookResponse findById(@PathVariable Long id) {
        return bookService.findById(id);
    }

    @Operation(summary="도서 등록")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookResponse create(@RequestBody BookCreateRequest request) {
        return bookService.create(request);
    }

    @Operation(summary="도서 삭제", description ="해당 아이디의 도서를 삭제합니다.")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        bookService.delete(id);
    }
}
package com.example.server_study_2026.domain.book;

import com.example.server_study_2026.domain.book.dto.BookCreateRequest;
import com.example.server_study_2026.domain.book.dto.BookResponse;
import com.example.server_study_2026.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
@Tag(name = "Book", description = "도서 관리 API")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    // 도서 등록
    @PostMapping
    @Operation(summary = "도서 등록")
    public ResponseEntity<ApiResponse<BookResponse>> createBook(
            @Valid @RequestBody BookCreateRequest request
    ) {
        BookResponse response = bookService.createBook(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }

    // 도서 전체 조회
    @GetMapping
    @Operation(summary = "도서 목록 조회")
    public ApiResponse<List<BookResponse>> getBooks() {
        return ApiResponse.success(bookService.getBooks());
    }

    // 도서 단건 조회
    @GetMapping("/{id}")
    @Operation(summary = "도서 단건 조회")
    public ApiResponse<BookResponse> getBook(
            @PathVariable Long id
    ) {
        return ApiResponse.success(bookService.getBook(id));
    }

    // 도서 삭제
    @DeleteMapping("/{id}")
    @Operation(summary = "도서 삭제")
    public ApiResponse<Void> deleteBook(
            @PathVariable Long id
    ) {
        bookService.deleteBook(id);
        return ApiResponse.success();
    }
}

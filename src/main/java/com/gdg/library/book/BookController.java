package com.gdg.library.book;

import com.gdg.library.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
@Tag(name = "Books", description = "도서 등록, 조회, 삭제")
public class BookController {
    private final BookService bookService;

    @GetMapping
    @Operation(summary = "도서 목록 조회")
    public ApiResponse<List<BookResponse>> findAll() {
        return ApiResponse.ok(bookService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "도서 단건 조회")
    public ApiResponse<BookResponse> findById(@PathVariable Long id) {
        return ApiResponse.ok(bookService.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "도서 등록")
    public ApiResponse<BookResponse> create(@Valid @RequestBody BookCreateRequest request) {
        return ApiResponse.ok(bookService.create(request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "도서 삭제", description = "대출 기록이 있으면 409를 반환합니다.")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        bookService.delete(id);
        return ApiResponse.ok(null);
    }
}

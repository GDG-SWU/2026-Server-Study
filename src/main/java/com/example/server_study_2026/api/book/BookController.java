package com.example.server_study_2026.api.book;

import com.example.server_study_2026.api.book.dto.BookCreateRequest;
import com.example.server_study_2026.api.book.dto.BookResponse;
import com.example.server_study_2026.global.response.ApiResponse;
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

    @Operation(summary="도서 목록 조회", description = "전체 도서를 조회합니다. isBorrowed 값으로 대출 여부를 필터링 할 수 있습니다.")
    @GetMapping
    public ApiResponse<List<BookResponse>> findAll(@RequestParam(required = false) Boolean isBorrowed) {
        return ApiResponse.ok(bookService.findAll(isBorrowed));
    }

    @Operation(summary="도서 단건 조회", description = "해당 아이디의 도서를 조회합니다.")
    @GetMapping("/{id}")
    public ApiResponse<BookResponse> findById(@PathVariable Long id) {
        return ApiResponse.ok(bookService.findById(id));
    }

    @Operation(summary="제목으로 도서 조회", description = "제목에 검색어가 포함된 도서를 조회합니다.")
    @GetMapping("/search")
    public ApiResponse<List<BookResponse>> findByTitle(@RequestParam String title) { return ApiResponse.ok(bookService.findByTitle(title)); }

    @Operation(summary="도서 등록")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<BookResponse> create(@RequestBody BookCreateRequest request) {
        return ApiResponse.ok(bookService.create(request));
    }

    @Operation(summary="도서 삭제", description ="해당 아이디의 도서를 삭제합니다.")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        bookService.delete(id);
        return ApiResponse.ok();
    }
}
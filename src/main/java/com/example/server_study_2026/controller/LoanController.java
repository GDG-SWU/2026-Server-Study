package com.example.server_study_2026.controller;

import com.example.server_study_2026.domain.loan.dto.LoanCreateRequest;
import com.example.server_study_2026.domain.loan.dto.LoanResponse;
import com.example.server_study_2026.service.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.example.server_study_2026.global.response.ApiResponse;

@Tag(name = "대출 API", description = "대출 조회/등록/수정 관련 API")
@RestController
@RequestMapping("/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    // 도서 대출 신청
    @Operation(summary = "대출 신청", description = "해당 도서를 대출합니다.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<LoanResponse> create(@Valid @RequestBody LoanCreateRequest loan) {
        return ApiResponse.ok(loanService.create(loan));
    }

    // 특정 회원 대출 내역 조회
    @Operation(summary = "회원 대출 내역 조회", description = "해당 회원의 대출 내역을 조회합니다.")
    @GetMapping
    public ApiResponse<List<LoanResponse>> findByUserId(@RequestParam Long userId) {
        return ApiResponse.ok(loanService.findByUserId(userId));
    }

    // 도서 반납 처리
    @Operation(summary = "도서 반납", description = "해당 대출을 종료하여 도서를 반납처리 합니다.")
    @PutMapping("/{id}")
    public ApiResponse<LoanResponse> returnBook(@PathVariable Long id) {
        return ApiResponse.ok(loanService.returnBook(id));
    }
}
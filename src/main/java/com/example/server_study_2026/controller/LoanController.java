package com.example.server_study_2026.controller;

import com.example.server_study_2026.dto.LoanCreateRequest;
import com.example.server_study_2026.dto.LoanResponse;
import com.example.server_study_2026.global.response.ApiResponse;
import com.example.server_study_2026.service.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/loans")
@RequiredArgsConstructor
@Tag(name = "Loan API", description = "도서 대출/반납 관련 API")
public class LoanController {

    private final LoanService loanService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "도서 대출 신청", description = "토큰의 사용자 정보와 도서 ID를 받아 대출을 생성합니다.")
    public ApiResponse<LoanResponse> createLoan(
            @AuthenticationPrincipal String email,
            @RequestBody @Valid LoanCreateRequest request) {
        return ApiResponse.success(201, "도서 대출 성공", loanService.createLoan(email, request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "도서 반납 처리", description = "대출 ID를 받아 반납을 처리합니다. (본인 대출만 가능)")
    public ApiResponse<LoanResponse> returnLoan(
            @AuthenticationPrincipal String email,
            @PathVariable Long id) {
        return ApiResponse.success(loanService.returnLoan(email, id));
    }

    @GetMapping
    @Operation(summary = "회원 대출 내역 조회", description = "특정 회원의 대출 내역을 조회합니다.")
    public ApiResponse<List<LoanResponse>> findByUserId(@RequestParam Long userId) {
        return ApiResponse.success(loanService.findByUserId(userId));
    }

    @GetMapping("/active")
    @Operation(summary = "현재 대출 중인 도서 조회", description = "반납되지 않은 대출 목록을 조회합니다.")
    public ApiResponse<List<LoanResponse>> findActiveLoans() {
        return ApiResponse.success(loanService.findActiveLoans());
    }
}
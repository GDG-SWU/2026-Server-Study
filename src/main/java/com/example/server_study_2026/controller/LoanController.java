package com.example.server_study_2026.controller;

import com.example.server_study_2026.dto.LoanCreateRequest;
import com.example.server_study_2026.dto.LoanResponse;
import com.example.server_study_2026.service.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    @Operation(summary = "도서 대출 신청", description = "회원 ID와 도서 ID를 받아 대출을 생성합니다.")
    public LoanResponse createLoan(@RequestBody LoanCreateRequest request) {
        return loanService.createLoan(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "도서 반납 처리", description = "대출 ID를 받아 반납을 처리합니다.")
    public LoanResponse returnLoan(@PathVariable Long id) {
        return loanService.returnLoan(id);
    }

    @GetMapping
    @Operation(summary = "회원 대출 내역 조회", description = "특정 회원의 대출 내역을 조회합니다.")
    public List<LoanResponse> findByUserId(@RequestParam Long userId) {
        return loanService.findByUserId(userId);
    }

    @GetMapping("/active")
    @Operation(summary = "현재 대출 중인 도서 조회", description = "반납되지 않은 대출 목록을 조회합니다.")
    public List<LoanResponse> findActiveLoans() {
        return loanService.findActiveLoans();
    }
}
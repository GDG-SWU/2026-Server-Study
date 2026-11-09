package com.example.server_study_2026.domain.loan;

import com.example.server_study_2026.domain.loan.dto.LoanCreateRequest;
import com.example.server_study_2026.domain.loan.dto.LoanResponse;
import com.example.server_study_2026.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/loans")
@Tag(name = "Loan", description = "도서 대출 API")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    // 대출 신청
    @PostMapping
    @Operation(summary = "대출 신청")
    public ResponseEntity<ApiResponse<LoanResponse>> createLoan(
            @Valid @RequestBody LoanCreateRequest request
    ) {
        LoanResponse response = loanService.createLoan(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }

    // 도서 반납
    @PutMapping("/{id}")
    @Operation(summary = "도서 반납")
    public ApiResponse<LoanResponse> returnLoan(
            @PathVariable Long id
    ) {
        LoanResponse response = loanService.returnLoan(id);

        return ApiResponse.success(response);
    }

    // 대출 목록 조회
    @GetMapping
    @Operation(summary = "대출 목록 조회")
    public ApiResponse<List<LoanResponse>> getLoans() {
        return ApiResponse.success(loanService.getLoans());
    }

    // 대출 단건 조회
    @GetMapping("/{id}")
    @Operation(summary = "대출 단건 조회")
    public ApiResponse<LoanResponse> getLoan(
            @PathVariable Long id
    ) {
        return ApiResponse.success(loanService.getLoan(id));
    }

    // 대출 기록 삭제
    @DeleteMapping("/{id}")
    @Operation(summary = "대출 기록 삭제")
    public ApiResponse<Void> deleteLoan(
            @PathVariable Long id
    ) {
        loanService.deleteLoan(id);

        return ApiResponse.success();
    }
}
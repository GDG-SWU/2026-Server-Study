
package com.example.server_study_2026.domain.loan;

import com.example.server_study_2026.domain.loan.dto.LoanCreateRequest;
import com.example.server_study_2026.domain.loan.dto.LoanResponse;
import com.example.server_study_2026.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
            @Valid @RequestBody LoanCreateRequest request,
            @AuthenticationPrincipal Long userId
    ) {
        LoanResponse response = loanService.createLoan(request, userId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }

    // 도서 반납
    @PutMapping("/{id}")
    @Operation(summary = "도서 반납")
    public ApiResponse<LoanResponse> returnLoan(
            @PathVariable Long id,
            @AuthenticationPrincipal Long userId
    ) {
        return ApiResponse.success(loanService.returnLoan(id, userId));
    }

    // 대출 목록 조회
    @GetMapping
    @Operation(summary = "대출 목록 조회")
    public ApiResponse<List<LoanResponse>> getLoans(
            @AuthenticationPrincipal Long userId
    ) {
        return ApiResponse.success(loanService.getLoans(userId));
    }

    // 대출 단건 조회
    @GetMapping("/{id}")
    @Operation(summary = "대출 단건 조회")
    public ApiResponse<LoanResponse> getLoan(
            @PathVariable Long id,
            @AuthenticationPrincipal Long userId
    ) {
        return ApiResponse.success(loanService.getLoan(id, userId));
    }

    // 대출 기록 삭제
    @DeleteMapping("/{id}")
    @Operation(summary = "대출 기록 삭제")
    public ApiResponse<Void> deleteLoan(
            @PathVariable Long id,
            @AuthenticationPrincipal Long userId
    ) {
        loanService.deleteLoan(id, userId);
        return ApiResponse.success();
    }
}

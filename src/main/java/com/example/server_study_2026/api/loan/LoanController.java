package com.example.server_study_2026.api.loan;

import com.example.server_study_2026.api.loan.dto.LoanCreateRequest;
import com.example.server_study_2026.api.loan.dto.LoanResponse;
import com.example.server_study_2026.domain.loan.Loan;
import com.example.server_study_2026.global.response.ApiResponse;
import com.example.server_study_2026.service.loan.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "대출 API", description = "대출 조회/등록/수정 관련 API")
@RestController
@RequestMapping("/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    //도서 대출 신청
    @Operation(summary = "대출 신청", description = "해당 도서를 대출합니다.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<LoanResponse> create(@AuthenticationPrincipal Long userId, @Valid @RequestBody LoanCreateRequest request) {
        // 서비스에 토큰에서 꺼낸 userId와 request를 함께 넘겨줌.
        return ApiResponse.ok(loanService.create(userId, request));
    }

    //특정 회원 대출 내역 조회
    @Operation(summary = "회원 대출 내역 조회", description = "해당 회원의 대출 내역을 조회합니다.")
    @GetMapping
    public ApiResponse<List<LoanResponse>> findByUserId(@AuthenticationPrincipal Long userId) { //@RequestParam을 붙여 필수로 받아오기
        return ApiResponse.ok(loanService.findByUserId(userId));
    }

    //도서 반납 처리
    @Operation(summary = "도서 반납", description = "해당 대출을 종료하여 도서를 반납처리 합니다.")
    @PutMapping("/{id}")
    public ApiResponse<LoanResponse> returnBook(@AuthenticationPrincipal Long userId, @PathVariable Long id) {
        return ApiResponse.ok(loanService.returnBook(userId, id));
    }
}

package com.gdg.library.loan;

import com.gdg.library.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/loans")
@RequiredArgsConstructor
@Tag(name = "Loans", description = "대출 신청, 조회, 반납, 삭제")
public class LoanController {
    private final LoanService loanService;

    @GetMapping
    @Operation(summary = "회원별 대출 목록 조회")
    public ApiResponse<List<LoanResponse>> findAll(@RequestParam("userId") Long userId) {
        return ApiResponse.ok(loanService.findByUserId(userId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "대출 단건 조회")
    public ApiResponse<LoanResponse> findById(@PathVariable Long id) {
        return ApiResponse.ok(loanService.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "대출 신청", description = "userId, bookId를 받습니다. 반납 예정일은 대출일로부터 14일 뒤입니다. 이미 대출 중인 책이면 409를 반환합니다.")
    public ApiResponse<LoanResponse> create(@Valid @RequestBody LoanCreateRequest request) {
        return ApiResponse.ok(loanService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "도서 반납", description = "요청 본문은 없습니다. 서울 기준 오늘 날짜로 반납 처리합니다. 이미 반납했으면 409를 반환합니다.")
    public ApiResponse<LoanResponse> returnLoan(@PathVariable Long id) {
        return ApiResponse.ok(loanService.returnLoan(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "대출 기록 삭제", description = "미반납 대출을 삭제하면 도서를 대출 가능 상태로 되돌립니다.")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        loanService.delete(id);
        return ApiResponse.ok(null);
    }
}

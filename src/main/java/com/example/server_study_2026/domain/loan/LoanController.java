package com.example.server_study_2026.domain.loan;

import com.example.server_study_2026.domain.loan.dto.LoanCreateRequest;
import com.example.server_study_2026.domain.loan.dto.LoanResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

    @PostMapping
    @Operation(summary = "대출 신청")
    public ResponseEntity<LoanResponse> createLoan(
            @RequestBody LoanCreateRequest request
    ) {
        LoanResponse response = loanService.createLoan(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "도서 반납")
    public ResponseEntity<LoanResponse> returnLoan(
            @PathVariable Long id
    ) {
        LoanResponse response = loanService.returnLoan(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "대출 목록 조회")
    public ResponseEntity<List<LoanResponse>> getLoans() {
        return ResponseEntity.ok(loanService.getLoans());
    }

    @GetMapping("/{id}")
    @Operation(summary = "대출 단건 조회")
    public ResponseEntity<LoanResponse> getLoan(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(loanService.getLoan(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "대출 기록 삭제")
    public ResponseEntity<Void> deleteLoan(
            @PathVariable Long id
    ) {
        loanService.deleteLoan(id);
        return ResponseEntity.noContent().build();
    }



}

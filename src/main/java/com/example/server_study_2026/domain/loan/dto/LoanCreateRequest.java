package com.example.server_study_2026.domain.loan.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoanCreateRequest {

    private Long userId;
    private Long bookId;

}

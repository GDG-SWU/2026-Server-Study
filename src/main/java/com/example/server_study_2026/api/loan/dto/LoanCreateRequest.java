package com.example.server_study_2026.api.loan.dto;

//대출 신청(생성) Request Dto
public record LoanCreateRequest (Long userId, Long bookId){
}

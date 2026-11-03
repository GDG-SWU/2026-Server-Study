package com.example.server_study_2026.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

// common/exception/ErrorCode.java
@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 공통
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "C001", "입력값이 올바르지 않습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C999", "서버 내부 오류입니다."),

    // 회원
    STUDENT_NOT_FOUND(HttpStatus.NOT_FOUND, "S001", "존재하지 않는 회원입니다."),   // ✅ 추가

    // 도서
    BOOK_NOT_FOUND(HttpStatus.NOT_FOUND, "B001", "존재하지 않는 도서입니다."),

    // 대출
    LOAN_NOT_FOUND(HttpStatus.NOT_FOUND, "L001", "존재하지 않는 대출 기록입니다."),
    BOOK_ALREADY_LOANED(HttpStatus.CONFLICT, "L002", "이미 대출 중인 도서입니다."),
    LOAN_ALREADY_RETURNED(HttpStatus.CONFLICT, "L003", "이미 반납된 대출입니다.");   // 마지막만 ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
package com.example.server_study_2026.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

// common/exception/ErrorCode.java
@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 공통
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "입력값이 올바르지 않습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다."),

    // 회원
    STUDENT_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."),   // ✅ 추가

    // 도서
    BOOK_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 도서입니다."),

    // 대출
    LOAN_NOT_FOUND(HttpStatus.NOT_FOUND,  "존재하지 않는 대출 기록입니다."),
    BOOK_ALREADY_LOANED(HttpStatus.CONFLICT,  "이미 대출 중인 도서입니다."),
    LOAN_ALREADY_RETURNED(HttpStatus.CONFLICT, "이미 반납된 대출입니다."),  // 마지막만 ;

    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다. 다시 로그인해주세요."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),

    //회원가입/로그인
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 가입된 이메일입니다.");

    private final HttpStatus status;
    private final String message;

    public String getCode() {   // 상수 이름을 그대로 코드로 사용
        return name();          // 예 : BOOK_NOT_FOUND → "BOOK_NOT_FOUND"
    }
}

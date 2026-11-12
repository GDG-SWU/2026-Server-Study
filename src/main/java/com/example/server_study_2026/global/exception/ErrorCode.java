package com.example.server_study_2026.global.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    // 404 NOT FOUND
    BOOK_NOT_FOUND(HttpStatus.NOT_FOUND, "도서를 찾을 수 없습니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다."),
    LOAN_NOT_FOUND(HttpStatus.NOT_FOUND, "대출 기록을 찾을 수 없습니다."),

    // 409 CONFLICT
    BOOK_ALREADY_LOANED(HttpStatus.CONFLICT, "이미 대출 중인 도서입니다."),
    LOAN_ALREADY_RETURNED(HttpStatus.CONFLICT, "이미 반납 처리된 대출입니다."),

    // 400 BAD REQUEST
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "잘못된 입력입니다."),

    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),

    // 500 INTERNAL SERVER ERROR
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");


    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getCode() {
        return this.name();
    }
}
package com.example.server_study_2026.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // 400 BAD_REQUEST
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, 400, "잘못된 입력값입니다."),

    // 404 NOT_FOUND
    BOOK_NOT_FOUND(HttpStatus.NOT_FOUND, 404, "존재하지 않는 도서입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, 404, "존재하지 않는 회원입니다."),
    LOAN_NOT_FOUND(HttpStatus.NOT_FOUND, 404, "존재하지 않는 대출 내역입니다."),

    // 409 CONFLICT
    ALREADY_BORROWED(HttpStatus.CONFLICT, 409, "이미 대출 중인 도서입니다."),
    ALREADY_RETURNED(HttpStatus.CONFLICT, 409, "이미 반납된 도서입니다."),

    // 6주차
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, 409, "이미 존재하는 이메일입니다."),
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, 401, "이메일 또는 비밀번호가 일치하지 않습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, 401, "인증 정보가 없거나 유효하지 않습니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, 403, "권한이 없습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, 401, "유효하지 않은 토큰입니다.");

    private final HttpStatus status;
    private final int code;
    private final String message;

    ErrorCode(HttpStatus status, int code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
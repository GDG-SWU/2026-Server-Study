package com.example.server_study_2026.global.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());   // 로그에 메시지가 찍히도록
        this.errorCode = errorCode;
    }
}
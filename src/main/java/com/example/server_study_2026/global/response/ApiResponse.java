package com.example.server_study_2026.global.response;

import com.example.server_study_2026.global.exception.ErrorCode;

import com.example.server_study_2026.global.exception.ErrorCode;

public record ApiResponse<T>(
        boolean success,   // 성공 여부
        String code,       // 결과 코드 (예: SUCCESS, B001)
        String message,    // 사람이 읽을 메시지
        T data             // 실제 데이터 (실패 시 null 또는 에러 상세)
) {
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, "SUCCESS", "요청 성공", data);
    }

    public static ApiResponse<Void> fail(ErrorCode errorCode) {
        return new ApiResponse<>(false, errorCode.getCode(), errorCode.getMessage(), null);
    }

    public static <T> ApiResponse<T> fail(ErrorCode errorCode, T data) {   // 검증 실패 상세용
        return new ApiResponse<>(false, errorCode.getCode(), errorCode.getMessage(), data);
    }
}
package com.example.server_study_2026.global.response;

import lombok.Getter;

@Getter
public class ApiResponse<T> {

    private boolean success;
    private String code;
    private String message;
    private T data;

    private ApiResponse(boolean success, String code, String message, T data) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(
                true,
                "SUCCESS",
                "요청이 성공했습니다.",
                data
        );
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(
                true,
                "SUCCESS",
                message,
                data
        );
    }

    public static ApiResponse<Void> success() {
        return new ApiResponse<>(
                true,
                "SUCCESS",
                "요청이 성공했습니다.",
                null
        );
    }

    public static ApiResponse<Void> error(String code, String message) {
        return new ApiResponse<>(
                false,
                code,
                message,
                null
        );
    }
}
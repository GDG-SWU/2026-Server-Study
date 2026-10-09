package com.example.server_study_2026.global.response;

//<T>: 데이터 자리 타입을 쓰는 사람이 정함
public record ApiResponse<T>(boolean success, String code, String message, T data) {
    //성공(돌려줄 데이터 O)
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, "SUCCESS", "요청이 성공했습니다.", data);
    }

    //성공(돌려줄 데이터 X)
    public static <T> ApiResponse<T> ok() {
        return new ApiResponse<>(true, "SUCCESS", "요청이 성공했습니다.", null);
    }

    //실패
    public static <T> ApiResponse<T> error(String code, String message) {
        return new ApiResponse<>(false, code, message, null);
    }
}

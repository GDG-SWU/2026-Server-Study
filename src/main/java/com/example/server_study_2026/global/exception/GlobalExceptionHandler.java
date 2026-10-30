package com.example.server_study_2026.global.exception;

import com.example.server_study_2026.global.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. 비즈니스 예외 처리
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();
        ApiResponse<Void> response = ApiResponse.error(errorCode.getCode(), errorCode.getMessage());
        return new ResponseEntity<>(response, errorCode.getStatus());
    }

    // 2. Validation(@Valid) 검증 실패 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        ApiResponse<Void> response = ApiResponse.error(400, message);
        return ResponseEntity.badRequest().body(response);
    }

    // 3. 잘못된 인자 값 예외 (400 또는 404로 처리)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException e) {
        ApiResponse<Void> response = ApiResponse.error(400, e.getMessage());
        return ResponseEntity.status(400).body(response);
    }

    // 4. 상태 충돌 예외 (이미 대출/반납된 경우 등 -> 409)
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalStateException(IllegalStateException e) {
        ApiResponse<Void> response = ApiResponse.error(409, e.getMessage());
        return ResponseEntity.status(409).body(response);
    }

    // 5. 그 외 예상치 못한 서버 예외 처리 (500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e, HttpServletRequest request) throws Exception {
        String uri = request.getRequestURI();
        if (uri != null && (uri.contains("/v3/api-docs") || uri.contains("/swagger-ui"))) {
            throw e;
        }

        ApiResponse<Void> response = ApiResponse.error(500, "서버 내부 오류가 발생했습니다.");
        return ResponseEntity.status(500).body(response);
    }
}
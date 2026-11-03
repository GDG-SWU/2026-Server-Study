package com.gdg.library.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Object> handleBusiness(BusinessException exception) {
        ErrorCode error = exception.getErrorCode();
        return ResponseEntity.status(error.getStatus())
                .body(ApiResponse.fail(error.name(), error.getMessage()));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .sorted().reduce((a, b) -> a + ", " + b).orElse("요청 값이 올바르지 않습니다.");
        return ResponseEntity.badRequest().body(ApiResponse.fail("INVALID_REQUEST", message));
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception exception, Object body, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        String code;
        String message;
        switch (status.value()) {
            case 400 -> {
                code = "INVALID_REQUEST";
                message = "요청 형식과 필수 파라미터를 확인해주세요.";
            }
            case 404 -> {
                code = "NOT_FOUND";
                message = "요청한 경로를 찾을 수 없습니다.";
            }
            case 405 -> {
                code = "METHOD_NOT_ALLOWED";
                message = "지원하지 않는 HTTP 메서드입니다.";
            }
            default -> {
                code = "HTTP_ERROR";
                message = "요청을 처리할 수 없습니다.";
            }
        }
        if (status.is5xxServerError()) {
            log.error("Spring 요청 처리 중 서버 오류가 발생했습니다.", exception);
        }
        return new ResponseEntity<>(ApiResponse.fail(code, message), headers, status);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleIntegrity(DataIntegrityViolationException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.fail(ErrorCode.DATA_CONFLICT.name(), ErrorCode.DATA_CONFLICT.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleUnexpected(Exception exception) {
        log.error("예상하지 못한 오류가 발생했습니다.", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail(ErrorCode.INTERNAL_SERVER_ERROR.name(),
                        ErrorCode.INTERNAL_SERVER_ERROR.getMessage()));
    }
}

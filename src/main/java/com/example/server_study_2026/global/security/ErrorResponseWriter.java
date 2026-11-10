package com.example.server_study_2026.global.security;

import com.example.server_study_2026.global.exception.ErrorCode;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ErrorResponseWriter {
    private ErrorResponseWriter() {}

    public static void write(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setStatus(errorCode.getStatus().value());
        response.setContentType("application/json; charset=utf-8");

        String body = String.format(
                "{\"success\":false,\"code\":\"%s\",\"message\":\"%s\",\"data\":null}",
                errorCode.name(), errorCode.getMessage());

        response.getWriter().write(body);
    }
}
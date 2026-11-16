package com.example.server_study_2026.global.security;

import com.example.server_study_2026.global.exception.ErrorCode;
import com.example.server_study_2026.global.response.ApiResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class ErrorResponseWriter {
    private final ObjectMapper mapper;

    public void write(HttpServletResponse response, ErrorCode error) throws IOException {
        response.setStatus(error.getStatus().value());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        mapper.writeValue(response.getOutputStream(), ApiResponse.fail(error.name(), error.getMessage()));
    }
}

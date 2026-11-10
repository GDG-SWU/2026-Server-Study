package com.example.server_study_2026.global.security;

import com.example.server_study_2026.global.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {


        ErrorCode errorCode = (ErrorCode) request.getAttribute("authError");
        if (errorCode == null) {
            errorCode = ErrorCode.UNAUTHORIZED;
        }
        ErrorResponseWriter.write(response, errorCode);
    }
}
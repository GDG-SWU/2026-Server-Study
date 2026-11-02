package com.example.server_study_2026.global.security;

import com.example.server_study_2026.global.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

//401(로그인 정보 없음)
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        // 필터에서 "만료"/"위조"로 표시해둔 게 있으면 그걸 사용, 없으면 일반 401
        ErrorCode errorCode = (ErrorCode) request.getAttribute("authError");
        if (errorCode == null) {
            errorCode = ErrorCode.UNAUTHORIZED;
        }
        ErrorResponseWriter.write(response, errorCode);
    }
}

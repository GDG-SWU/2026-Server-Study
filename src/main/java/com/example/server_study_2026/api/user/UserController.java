package com.example.server_study_2026.api.user;

import com.example.server_study_2026.api.user.dto.LoginRequest;
import com.example.server_study_2026.api.user.dto.SignupRequest;
import com.example.server_study_2026.api.user.dto.TokenResponse;
import com.example.server_study_2026.api.user.dto.UserResponse;
import com.example.server_study_2026.global.response.ApiResponse;
import com.example.server_study_2026.service.user.UserService;
import io.jsonwebtoken.Jwt;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name="회원 API", description = "회원가입/로그인 API")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(summary = "회원가입")
    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<UserResponse> signup(@Valid @RequestBody SignupRequest request) {
        return ApiResponse.ok(userService.signup(request));
    }

    @Operation(summary="로그인", description = "성공시 JWT access token을 발급합니다.")
    @PostMapping("/login")
    public ApiResponse<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok(userService.login(request));
    }

    @Operation(summary="내 정보 조회", description = "해당 토큰 주인의 정보를 조회합니다")
    @GetMapping("/me")
    public ApiResponse<UserResponse> me(@AuthenticationPrincipal Long userId) {
        return ApiResponse.ok(userService.findById(userId));
    }
}

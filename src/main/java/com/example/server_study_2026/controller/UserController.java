package com.example.server_study_2026.controller;

import com.example.server_study_2026.domain.User;
import com.example.server_study_2026.dto.TokenResponse;
import com.example.server_study_2026.dto.LoginRequest;
import com.example.server_study_2026.dto.UserResponse;
import com.example.server_study_2026.dto.SignupRequest;
import com.example.server_study_2026.global.response.ApiResponse;
import com.example.server_study_2026.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signup(@RequestBody @Valid SignupRequest request) {
        userService.signup(request);
        return ResponseEntity.status(201).body(ApiResponse.success(null));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@RequestBody @Valid LoginRequest request) {
        String token = userService.login(request);
        return ResponseEntity.ok(ApiResponse.success(new TokenResponse(token)));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMyInfo(@AuthenticationPrincipal String email) {
        User user = userService.getLoginUser(email);
        return ResponseEntity.ok(ApiResponse.success(new UserResponse(user)));
    }
}
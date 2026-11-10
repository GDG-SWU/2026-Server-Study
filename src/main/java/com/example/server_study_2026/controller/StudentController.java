package com.example.server_study_2026.controller;


import com.example.server_study_2026.domain.student.Student;
import com.example.server_study_2026.domain.student.dto.SignupRequest;
import com.example.server_study_2026.domain.student.dto.LoginRequest;
import com.example.server_study_2026.domain.student.dto.StudentResonse;
import com.example.server_study_2026.domain.student.dto.TokenResponse;
import com.example.server_study_2026.global.response.ApiResponse;
import com.example.server_study_2026.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "회원 API", description = "회원가입 / 로그인 API")
@RestController
@RequestMapping("/students")
@RequiredArgsConstructor

public class StudentController {
    private final StudentService studentService;

    @Operation(summary = "회원가입")
    @PostMapping ("/signup")
    public ApiResponse<StudentResonse> signup(@Valid @RequestBody SignupRequest request) {
        return ApiResponse.ok(studentService.signup(request));
    }

    @Operation (summary = "로그인", description = "성공 시 JWT access token을 발급합니다.")
    @PostMapping ("/login")
    public ApiResponse<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok(studentService.login(request));
    }

    @Operation (summary = "내 정보 조회", description = "해당 토큰 주인의 정보를 조회합니다")
    @GetMapping  ("/me")
    public ApiResponse <StudentResonse> me(@AuthenticationPrincipal Long studentId) {
        return ApiResponse.ok(studentService.findById(studentId));
    }

}

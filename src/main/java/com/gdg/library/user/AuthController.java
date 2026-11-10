package com.gdg.library.user;

import com.gdg.library.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Users")
public class AuthController {
    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/users/signup")
    @ResponseStatus(HttpStatus.CREATED)
    @SecurityRequirements
    @Operation(summary = "회원가입", description = "항상 USER로 가입하며 비밀번호는 BCrypt로 저장합니다.")
    public ApiResponse<UserResponse> signup(@Valid @RequestBody UserCreateRequest request) {
        return ApiResponse.ok(userService.create(request));
    }


    @PostMapping("/users/login")
    @SecurityRequirements
    @Operation(summary = "로그인 및 JWT 발급")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok(authService.login(request));
    }
}

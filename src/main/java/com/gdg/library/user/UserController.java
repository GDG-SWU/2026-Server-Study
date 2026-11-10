package com.gdg.library.user;

import com.gdg.library.common.ApiResponse;
import com.gdg.library.security.LoginUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "회원 등록, 조회, 삭제")
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "내 정보 조회")
    public ApiResponse<UserResponse> me(
            @AuthenticationPrincipal LoginUser user) {
        return ApiResponse.ok(userService.findById(user.id()));
    }

    @GetMapping
    @Operation(summary = "회원 목록 조회")
    public ApiResponse<List<UserResponse>> findAll() {
        return ApiResponse.ok(userService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "회원 단건 조회")
    public ApiResponse<UserResponse> findById(@PathVariable Long id) {
        return ApiResponse.ok(userService.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "회원 등록", description = "이메일과 비밀번호를 받습니다. 중복 이메일이면 409를 반환하며, 비밀번호는 응답에 포함하지 않습니다.")
    public ApiResponse<UserResponse> create(@Valid @RequestBody UserCreateRequest request) {
        return ApiResponse.ok(userService.create(request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "회원 삭제", description = "대출 기록이 있으면 409를 반환합니다.")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ApiResponse.ok(null);
    }
}

package com.gdg.library.user;

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

    @GetMapping
    @Operation(summary = "회원 목록 조회")
    public List<UserResponse> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "회원 단건 조회")
    public UserResponse findById(@PathVariable Long id) {
        return userService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "회원 등록", description = "이메일과 비밀번호를 받습니다. 중복 이메일이면 409를 반환하며, 비밀번호는 응답에 포함하지 않습니다.")
    public UserResponse create(@Valid @RequestBody UserCreateRequest request) {
        return userService.create(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "회원 삭제", description = "대출 기록이 있으면 409를 반환합니다.")
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }
}

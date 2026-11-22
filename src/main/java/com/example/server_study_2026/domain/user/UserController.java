
package com.example.server_study_2026.domain.user;

import com.example.server_study_2026.domain.user.dto.LoginRequest;
import com.example.server_study_2026.domain.user.dto.SignupRequest;
import com.example.server_study_2026.global.response.ApiResponse;
import com.example.server_study_2026.global.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    public UserController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signup(
            @Valid @RequestBody SignupRequest request
    ) {
        userService.signup(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("회원가입이 완료되었습니다.", null));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, String>>> login(
            @Valid @RequestBody LoginRequest request
    ) {
        User user = userService.login(request);
        String token = jwtService.generateToken(user);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "로그인에 성공했습니다.",
                        Map.of("accessToken", token)
                )
        );
    }
}

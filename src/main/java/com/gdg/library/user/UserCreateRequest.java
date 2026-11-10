package com.gdg.library.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.AssertTrue;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.nio.charset.StandardCharsets;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

public record UserCreateRequest(
        @NotBlank @Email @Size(max = 100) @Schema(example = "sua@example.com") String email,
        @NotBlank @Size(max = 255)
        @Schema(example = "practice-password", accessMode = Schema.AccessMode.WRITE_ONLY) String password
) {
    // BCrypt는 UTF-8 기준 최대 72바이트를 처리한다.
    @JsonIgnore
    @AssertTrue(message = "비밀번호는 UTF-8 기준 72바이트 이하여야 합니다.")
    public boolean isPasswordWithinByteLimit() {
        return password == null || password.getBytes(StandardCharsets.UTF_8).length <= 72;
    }
}

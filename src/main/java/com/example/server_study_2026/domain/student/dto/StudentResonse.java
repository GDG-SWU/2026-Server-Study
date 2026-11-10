package com.example.server_study_2026.domain.student.dto;

import com.example.server_study_2026.domain.student.Role;
import com.example.server_study_2026.domain.student.Student;
import jakarta.validation.constraints.NotBlank;

public record StudentResonse(
    Long id,
    Role role,
    String email
) {
    public static StudentResonse from(Student student) {
        return new StudentResonse(
                student.getId(),
                student.getRole(),
                student.getEmail()
        );
    }

    public static record LoginRequest(
        @NotBlank(message = "이메일은 필수입니다.")
        String email,

        @NotBlank (message = "비밀번호는 필수입니다.")
        String password ){
    }
}

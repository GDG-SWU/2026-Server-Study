package com.example.server_study_2026.domain.student.loan;

import com.example.server_study_2026.domain.student.Student;

public record StudentResonse(
    Long id,
    String password,
    String email
) {
    public static StudentResonse from(Student student) {
        return new StudentResonse(
                student.getId(),
                student.getPassword(),
                student.getEmail()
        );
    }
}

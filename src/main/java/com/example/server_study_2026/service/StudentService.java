package com.example.server_study_2026.service;

import com.example.server_study_2026.domain.student.Student;
import com.example.server_study_2026.domain.student.StudentRepository;
import com.example.server_study_2026.domain.student.dto.LoginRequest;
import com.example.server_study_2026.domain.student.dto.SignupRequest;
import com.example.server_study_2026.domain.student.dto.StudentResonse;
import com.example.server_study_2026.domain.student.dto.TokenResponse;
import com.example.server_study_2026.global.exception.BusinessException;
import com.example.server_study_2026.global.exception.ErrorCode;
import com.example.server_study_2026.global.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentService {
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Transactional
    public StudentResonse signup(SignupRequest request) {
        if (studentRepository.existsByEmail(request.email())) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }

        Student student = Student.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .build();

        return StudentResonse.from(studentRepository.save(student));
    }

    public TokenResponse login(LoginRequest request) {
        Student student = studentRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException(ErrorCode.LOGIN_FAILED));

        if(!passwordEncoder.matches(request.password(), student.getPassword())) {
            throw new BusinessException(ErrorCode.LOGIN_FAILED);
        }

        return TokenResponse.of(jwtProvider.createToken(student.getId(), student.getRole()));
    }

    public StudentResonse findById(Long id) {
        Student student = studentRepository.findById(id).orElseThrow(() -> new BusinessException(ErrorCode.STUDENT_NOT_FOUND));
        return StudentResonse.from(student);
    }
}
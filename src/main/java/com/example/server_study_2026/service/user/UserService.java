package com.example.server_study_2026.service.user;

import com.example.server_study_2026.api.user.dto.LoginRequest;
import com.example.server_study_2026.api.user.dto.SignupRequest;
import com.example.server_study_2026.api.user.dto.TokenResponse;
import com.example.server_study_2026.api.user.dto.UserResponse;
import com.example.server_study_2026.domain.user.User;
import com.example.server_study_2026.domain.user.UserRepository;
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
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Transactional
    public UserResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .build();

        return UserResponse.from(userRepository.save(user));
    }

    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException(ErrorCode.LOGIN_FAILED));

        if(!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BusinessException(ErrorCode.LOGIN_FAILED);
        }

        return TokenResponse.of(jwtProvider.createToken(user.getId(), user.getRole()));
    }

    public UserResponse findById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return UserResponse.from(user);
    }
}

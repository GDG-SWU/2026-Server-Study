
package com.example.server_study_2026.domain.user;

import com.example.server_study_2026.domain.user.dto.LoginRequest;
import com.example.server_study_2026.domain.user.dto.SignupRequest;
import com.example.server_study_2026.global.exception.BusinessException;
import com.example.server_study_2026.global.exception.ErrorCode;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 회원가입
    public void signup(SignupRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }

        String encodedPassword =
                passwordEncoder.encode(request.getPassword());

        User user = new User(
                request.getEmail(),
                encodedPassword
        );

        userRepository.save(user);
    }

    // 로그인
    @Transactional(readOnly = true)
    public User login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new BusinessException(ErrorCode.LOGIN_FAILED));

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        )) {
            throw new BusinessException(ErrorCode.LOGIN_FAILED);
        }

        return user;
    }
}

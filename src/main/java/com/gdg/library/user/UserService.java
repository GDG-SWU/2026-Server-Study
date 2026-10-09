package com.gdg.library.user;

import com.gdg.library.common.ApiException;
import com.gdg.library.loan.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final LoanRepository loanRepository;
    private final PasswordEncoder passwordEncoder;
    private final Clock clock;

    public List<UserResponse> findAll() {
        return userRepository.findAll().stream().map(UserResponse::from).toList();
    }

    public UserResponse findById(Long id) {
        return UserResponse.from(userRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("회원", id)));
    }

    @Transactional
    public UserResponse create(UserCreateRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw ApiException.conflict("이미 등록된 이메일입니다.");
        }
        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .createdAt(LocalDateTime.now(clock))
                .build();
        // DB의 UNIQUE 제약은 동시 회원 등록 시에도 중복 이메일을 막는다.
        return UserResponse.from(userRepository.save(user));
    }

    @Transactional
    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("회원", id));
        if (loanRepository.existsByUserId(id)) {
            throw ApiException.conflict("대출 기록이 있는 회원은 삭제할 수 없습니다.");
        }
        userRepository.delete(user);
    }
}


package com.example.server_study_2026.global.config;

import com.example.server_study_2026.global.security.JwtAuthenticationFilter;
import com.example.server_study_2026.global.security.JwtService;
import com.example.server_study_2026.global.security.SecurityExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtService jwtService,
            SecurityExceptionHandler securityExceptionHandler
    ) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                // 세션을 사용하지 않는 JWT 방식
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // API별 접근 권한 설정
                .authorizeHttpRequests(auth -> auth

                        // 회원가입, 로그인, 헬스 체크는 인증 없이 접근 가능
                        .requestMatchers(
                                "/users/signup",
                                "/users/login",
                                "/health"
                        ).permitAll()

                        // 도서 등록은 ADMIN만 가능
                        .requestMatchers(HttpMethod.POST, "/books")
                        .hasRole("ADMIN")

                        // 도서 삭제는 ADMIN만 가능
                        .requestMatchers(HttpMethod.DELETE, "/books/**")
                        .hasRole("ADMIN")

                        // 도서 조회는 USER 또는 ADMIN 가능
                        .requestMatchers(HttpMethod.GET, "/books/**")
                        .hasAnyRole("USER", "ADMIN")

                        // 나머지 API는 로그인 필요
                        .anyRequest().authenticated()
                )

                // 401, 403 응답 처리
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(securityExceptionHandler)
                        .accessDeniedHandler(securityExceptionHandler)
                )

                // JWT 인증 필터 등록
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtService),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}

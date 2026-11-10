package com.gdg.library.security;

import org.springframework.context.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Profile("dev")
public class DevH2SecurityConfig {
    @Bean
    @Order(1)
    public SecurityFilterChain h2ConsoleSecurity(HttpSecurity http) throws Exception {
        // 로컬 dev 프로필의 H2 콘솔만 허용한다. API는 기본 JWT 필터 체인을 사용한다.
        return http.securityMatcher("/h2-console/**")
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll()).build();
    }
}

package com.gdg.library;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import java.security.SecureRandom;
import java.util.Base64;

abstract class ApiTestSupport {
    static final String SECRET;
    static {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        SECRET = Base64.getEncoder().encodeToString(bytes);
    }

    @DynamicPropertySource
    static void jwtProperties(DynamicPropertyRegistry registry) {
        registry.add("jwt.secret", () -> SECRET);
    }
}

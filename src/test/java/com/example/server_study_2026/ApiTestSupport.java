package com.example.server_study_2026;

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
        registry.add("spring.datasource.url", () -> "jdbc:h2:mem:library;DB_CLOSE_DELAY=-1");
        registry.add("spring.datasource.username", () -> "sa");
        registry.add("spring.datasource.password", () -> "");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.jpa.properties.hibernate.globally_quoted_identifiers", () -> "false");
    }
}

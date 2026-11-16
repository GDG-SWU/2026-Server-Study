package com.example.server_study_2026;

import com.example.server_study_2026.domain.user.User;
import com.example.server_study_2026.domain.user.UserRepository;
import com.example.server_study_2026.global.common.BaseEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.Clock;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ServerStudy2026ApplicationTests extends ApiTestSupport {
    @Autowired UserRepository users;
    @Autowired Clock clock;

    @Test
    void baseEntityAuditsRepositoryWritesWithoutService() {
        String email = "audit-" + java.util.UUID.randomUUID() + "@example.com";
        LocalDateTime before = LocalDateTime.now(clock).minusSeconds(1);
        User user = users.saveAndFlush(User.builder().email(email).password("test-hash").build());
        try {
            assertThat(user).isInstanceOf(BaseEntity.class);
            assertThat(user.getCreatedAt()).isBetween(before, LocalDateTime.now(clock).plusSeconds(1));
            assertThat(users.findById(user.getId()).orElseThrow().getCreatedAt()).isNotNull();
        } finally {
            users.deleteById(user.getId());
        }
    }
}

package com.example.server_study_2026;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class ServerStudy2026Application {

    public static void main(String[] args) {
        SpringApplication.run(ServerStudy2026Application.class, args);
    }

}

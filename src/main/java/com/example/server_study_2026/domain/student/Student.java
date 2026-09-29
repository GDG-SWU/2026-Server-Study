package com.example.server_study_2026.domain.student;

import com.example.server_study_2026.global.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "Student")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Student extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Builder
    public Student(String email, String password){
        this.email = email;
        this.password = password;
    }
}
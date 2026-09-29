package com.example.server_study_2026.global;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter //다른 클래스에서 getCreateAt 메서드를 사용할 수 있게 자동으로 만듦
@MappedSuperclass //자식클래스에서 가져가면 실제 DB 테이블에 생성해줌
@EntityListeners(AuditingEntityListener.class) //시간이 맞게 반영되는지 감시

//abstract : 추상클래스 선언. 오로지 상속만으로 이용한다.
public abstract class BaseEntity{

    @CreatedDate
    @Column(name = "created_at", updatable = false) //updatable : 수정 가능한지(방지위함)
    private LocalDateTime createdAt;
}
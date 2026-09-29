package com.example.server_study_2026.domain.loan;

import com.example.server_study_2026.domain.book.Book;
import com.example.server_study_2026.domain.student.Student;
import com.example.server_study_2026.global.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.apache.catalina.User;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "loan")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Loan{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "loan_date", nullable = false)
    private LocalDate loanDate;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "return_date") // 아직 반납하지 않았으면 null 허용
    private LocalDate returnDate;

    @Builder
    public Loan(Student student, Book book, LocalDate loanDate){
        this.student = student;
        this.book = book;
        this.loanDate = loanDate;
        this.dueDate = loanDate.plusDays(14); // 14일 반납기한 비즈니스 로직
    }

    // 반납 처리
    public void completeReturn(LocalDate returnDate){
        this.returnDate = returnDate;
        this.book.returnBook();
    }
}
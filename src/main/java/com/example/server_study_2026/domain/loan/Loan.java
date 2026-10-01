package com.example.server_study_2026.domain.loan;

import com.example.server_study_2026.domain.book.Book;
import com.example.server_study_2026.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name="loan")
@Getter
@NoArgsConstructor(access= AccessLevel.PROTECTED) //매개변수 없는 기본 생성자 자동생성
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;
    
    @Column(name = "loan_date", nullable = false)
    private LocalDate loanDate;
    
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;
    
    @Column(name = "return_date") //아직 반납하지 않았으면 null 허용
    private LocalDate returnDate;
    
    @Builder
    public Loan(User user, Book book, LocalDate loanDate) {
        this.user = user;
        this.book = book;
        this.loanDate = loanDate;
        this.dueDate = loanDate.plusDays(14); //반납 기한 +14일 비지니스 로직
    }
    
    public void completeReturn(LocalDate returnDate) {
        this.returnDate = returnDate;
        this.book.returnBook(); //returnBook() 메소드를 호출해 대출상태를 false로 바꿈
    }
}

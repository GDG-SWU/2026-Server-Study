package com.gdg.library.book;

import com.gdg.library.common.ApiException;
import com.gdg.library.loan.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {
    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;

    public List<BookResponse> findAll() {
        return bookRepository.findAll().stream().map(BookResponse::from).toList();
    }

    public BookResponse findById(Long id) {
        return BookResponse.from(bookRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("도서", id)));
    }

    @Transactional
    public BookResponse create(BookCreateRequest request) {
        Book book = Book.builder().title(request.title()).author(request.author()).build();
        return BookResponse.from(bookRepository.save(book));
    }

    @Transactional
    public void delete(Long id) {
        Book book = bookRepository.findByIdForUpdate(id)
                .orElseThrow(() -> ApiException.notFound("도서", id));
        if (loanRepository.existsByBookId(id)) {
            throw ApiException.conflict("대출 기록이 있는 도서는 삭제할 수 없습니다.");
        }
        bookRepository.delete(book);
    }
}

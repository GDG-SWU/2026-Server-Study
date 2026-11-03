package com.gdg.library.book;

import com.gdg.library.common.BusinessException;
import com.gdg.library.common.ErrorCode;
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
                .orElseThrow(() -> new BusinessException(ErrorCode.BOOK_NOT_FOUND)));
    }

    @Transactional
    public BookResponse create(BookCreateRequest request) {
        Book book = Book.builder().title(request.title()).author(request.author()).build();
        return BookResponse.from(bookRepository.save(book));
    }

    @Transactional
    public void delete(Long id) {
        Book book = bookRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOOK_NOT_FOUND));
        if (loanRepository.existsByBookId(id)) {
            throw new BusinessException(ErrorCode.BOOK_HAS_LOANS);
        }
        bookRepository.delete(book);
    }
}

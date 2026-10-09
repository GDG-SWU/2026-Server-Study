package com.gdg.library.book;

public record BookResponse(Long id, String title, String author, boolean isBorrowed) {
    public static BookResponse from(Book book) {
        return new BookResponse(book.getId(), book.getTitle(), book.getAuthor(), book.isBorrowed());
    }
}

package com.example.BookReview;

import com.bookreview.service.BookService;
import com.bookreview.entity.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class BookReviewApplicationTests {

    @Autowired
    private BookService bookService;

    @Test
    void contextLoads() {
        // Basic context load test
    }

    @Test
    void testBookServiceSaveAndGet() {
        Book book = new Book();
        book.setTitle("Test Book");
        book.setGenre("Fiction");
        book.setDescription("A test book");
        Book saved = bookService.saveBook(book);
        assertThat(saved.getId()).isNotNull();
        assertThat(bookService.getBook(saved.getId())).isPresent();
    }
}

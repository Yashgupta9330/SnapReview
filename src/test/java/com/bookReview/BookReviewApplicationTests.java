package com.example.BookReview;

import com.bookreview.service.BookService;
import com.bookreview.entity.Book;
import com.bookreview.entity.Genre;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.Collections;

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
        Genre genre = new Genre();
        genre.setName("Fiction");
        book.setGenres(Collections.singleton(genre));
        book.setDescription("A test book");
        var saved = bookService.saveBook(book); // BookDTO
        assertThat(saved.getId()).isNotNull();
        assertThat(bookService.getBook(saved.getId())).isPresent();
    }
}

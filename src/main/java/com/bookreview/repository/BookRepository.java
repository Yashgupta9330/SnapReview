package com.bookreview.repository;

import com.bookreview.entity.Book;
import com.bookreview.entity.BookStatus;
import com.bookreview.entity.User;
import com.bookreview.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByStatus(BookStatus status);
    List<Book> findByAuthor(User author);
    List<Book> findByGenresContaining(Genre genre);
    Optional<Book> findByTitle(String title);
}
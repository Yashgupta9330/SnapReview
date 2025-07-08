package com.bookreview.repository;

import com.bookreview.entity.Review;
import com.bookreview.entity.User;
import com.bookreview.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByUser(User user);
    List<Review> findByBook(Book book);
    List<Review> findByIsActiveTrue();
}
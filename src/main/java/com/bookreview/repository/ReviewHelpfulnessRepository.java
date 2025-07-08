package com.bookreview.repository;

import com.bookreview.entity.ReviewHelpfulness;
import com.bookreview.entity.User;
import com.bookreview.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface ReviewHelpfulnessRepository extends JpaRepository<ReviewHelpfulness, Long> {
    Optional<ReviewHelpfulness> findByUserAndReview(User user, Review review);
    List<ReviewHelpfulness> findByReview(Review review);
} 
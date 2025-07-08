package com.bookreview.service;

import com.bookreview.entity.Review;
import com.bookreview.entity.User;
import com.bookreview.entity.Book;
import com.bookreview.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;

    public List<Review> getReviewsByBook(Book book) {
        return reviewRepository.findByBook(book);
    }

    public List<Review> getReviewsByUser(User user) {
        return reviewRepository.findByUser(user);
    }

    public List<Review> getActiveReviews() {
        return reviewRepository.findByIsActiveTrue();
    }

    public Optional<Review> getReview(Long id) {
        return reviewRepository.findById(id);
    }

    @Transactional
    public Review updateReview(Long id, Review updatedReview) {
        return reviewRepository.findById(id).map(review -> {
            review.setContent(updatedReview.getContent());
            review.setRating(updatedReview.getRating());
            review.setTitle(updatedReview.getTitle());
            review.setIsActive(updatedReview.getIsActive());
            review.setIsFeatured(updatedReview.getIsFeatured());
            // ... update other fields as needed
            review.setUpdatedAt(java.time.LocalDateTime.now());
            return reviewRepository.save(review);
        }).orElseThrow(() -> new RuntimeException("Review not found"));
    }

    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }

    @Transactional
    public Review saveReview(Review review) {
        if (review.getId() == null) {
            review.setCreatedAt(java.time.LocalDateTime.now());
        }
        review.setUpdatedAt(java.time.LocalDateTime.now());
        return reviewRepository.save(review);
    }
}
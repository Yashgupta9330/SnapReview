package com.bookreview.service;

import com.bookreview.dto.ReviewDTO;
import com.bookreview.entity.Review;
import com.bookreview.entity.User;
import com.bookreview.entity.Book;
import com.bookreview.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    private ReviewDTO toDTO(Review review) {
        return new ReviewDTO(
            review.getId(),
            review.getContent(),
            review.getRating(),
            review.getTitle(),
            review.getCreatedAt(),
            review.getUpdatedAt(),
            review.getUser() != null ? review.getUser().getUsername() : null,
            review.getBook() != null ? review.getBook().getId() : null
        );
    }

    public List<ReviewDTO> getReviewsByBook(Book book) {
        return reviewRepository.findByBook(book).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<ReviewDTO> getReviewsByUser(User user) {
        return reviewRepository.findByUser(user).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<ReviewDTO> getActiveReviews() {
        return reviewRepository.findByIsActiveTrue().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public Optional<ReviewDTO> getReview(Long id) {
        return reviewRepository.findById(id).map(this::toDTO);
    }

    @Transactional
    public Optional<ReviewDTO> updateReview(Long id, Review updatedReview) {
        return reviewRepository.findById(id).map(review -> {
            review.setContent(updatedReview.getContent());
            review.setRating(updatedReview.getRating());
            review.setTitle(updatedReview.getTitle());
            review.setIsActive(updatedReview.getIsActive());
            review.setIsFeatured(updatedReview.getIsFeatured());
            return toDTO(reviewRepository.save(review));
        });
    }

    public void deleteReview(Long id) {
        if (!reviewRepository.existsById(id)) {
            throw new IllegalArgumentException("Review not found with id " + id);
        }
        reviewRepository.deleteById(id);
    }

    @Transactional
    public ReviewDTO saveReview(Review review) {
        if (reviewRepository.existsByUserAndBook(review.getUser(), review.getBook())) {
            throw new IllegalStateException("User has already reviewed this book");
        }
        return toDTO(reviewRepository.save(review));
    }
}

package com.bookreview.service;

import com.bookreview.dto.ReviewDTO;
import com.bookreview.entity.Review;
import com.bookreview.entity.User;
import com.bookreview.entity.Book;
import com.bookreview.repository.ReviewRepository;
import com.bookreview.repository.BookRepository;
import com.bookreview.dto.ReviewCreateRequest;
import com.bookreview.dto.ReviewUpdateRequest;
import com.bookreview.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.security.Principal;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

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

    public List<ReviewDTO> getReviewsByBookId(Long bookId) {
        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new EntityNotFoundException("Book with id " + bookId + " does not exist"));
        return reviewRepository.findByBook(book).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<ReviewDTO> getReviewsByUserId(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " does not exist"));
        return reviewRepository.findByUser(user).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public ReviewDTO createReview(ReviewCreateRequest request, Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new EntityNotFoundException("User not found: " + username));
        Book book = bookRepository.findById(request.getBookId())
            .orElseThrow(() -> new EntityNotFoundException("Book not found: " + request.getBookId()));
        if (reviewRepository.existsByUserAndBook(user, book)) {
            throw new IllegalStateException("User has already reviewed this book");
        }
        Review review = new Review();
        review.setTitle(request.getTitle());
        review.setContent(request.getContent());
        review.setRating(request.getRating());
        review.setUser(user);
        review.setBook(book);
        return toDTO(reviewRepository.save(review));
    }

    public Optional<ReviewDTO> updateReview(Long id, ReviewUpdateRequest request, Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new EntityNotFoundException("User not found: " + username));
        return reviewRepository.findById(id).map(review -> {
            if (!review.getUser().getId().equals(user.getId())) {
                throw new AccessDeniedException("You are not allowed to edit this review.");
            }
            review.setTitle(request.getTitle());
            review.setContent(request.getContent());
            review.setRating(request.getRating());
            return toDTO(reviewRepository.save(review));
        });
    }

    public boolean deleteReview(Long id, Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new EntityNotFoundException("User not found: " + username));
        Optional<Review> reviewOpt = reviewRepository.findById(id);
        if (reviewOpt.isPresent()) {
            Review review = reviewOpt.get();
            if (!review.getUser().getId().equals(user.getId())) {
                throw new AccessDeniedException("You are not allowed to delete this review.");
            }
            reviewRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Transactional
    public ReviewDTO saveReview(Review review) {
        if (reviewRepository.existsByUserAndBook(review.getUser(), review.getBook())) {
            throw new IllegalStateException("User has already reviewed this book");
        }
        return toDTO(reviewRepository.save(review));
    }

    public boolean isReviewAuthor(Long reviewId, String username) {
        Optional<Review> reviewOpt = reviewRepository.findById(reviewId);
        return reviewOpt.isPresent() && reviewOpt.get().getUser().getUsername().equals(username);
    }
}

package com.bookreview.controller;

import com.bookreview.dto.ReviewDTO;
import com.bookreview.entity.Review;
import com.bookreview.entity.User;
import com.bookreview.entity.Book;
import com.bookreview.repository.UserRepository;
import com.bookreview.repository.BookRepository;
import com.bookreview.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.security.Principal;
import jakarta.persistence.EntityNotFoundException;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<ReviewDTO>> getReviewsByBook(@PathVariable Long bookId) {
        return bookRepository.findById(bookId)
                .map(book -> ResponseEntity.ok(reviewService.getReviewsByBook(book)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewDTO> getReview(@PathVariable Long id) {
        return reviewService.getReview(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReviewDTO>> getReviewsByUser(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " does not exist"));
        return ResponseEntity.ok(reviewService.getReviewsByUser(user));
    }

    @PostMapping
    public ResponseEntity<ReviewDTO> createReview(@Valid @RequestBody Review review, Principal principal) {
        // Set the user to the currently authenticated user
        String username = principal.getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        review.setUser(user);
        // Ensure the book is managed
        if (review.getBook() != null && review.getBook().getId() != null) {
            Book managedBook = bookRepository.findById(review.getBook().getId())
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + review.getBook().getId()));
            review.setBook(managedBook);
        }
        return ResponseEntity.ok(reviewService.saveReview(review));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReviewDTO> updateReview(@PathVariable Long id, @Valid @RequestBody Review review, Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        return reviewService.updateReview(id, review, user)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('MODERATOR') or @reviewService.isReviewAuthor(#id, authentication.name)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "deleted successfully");
        return ResponseEntity.ok(response);
    }
}

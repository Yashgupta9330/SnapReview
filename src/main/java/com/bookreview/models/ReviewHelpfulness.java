package com.bookreview.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "review_helpfulness", indexes = {
    @Index(name = "idx_review_helpfulness_user", columnList = "user_id"),
    @Index(name = "idx_review_helpfulness_review", columnList = "review_id")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uk_user_review_helpfulness", columnNames = {"user_id", "review_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"user", "review"})
@ToString(exclude = {"user", "review"})
public class ReviewHelpfulness {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "is_helpful", nullable = false)
    private Boolean isHelpful;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
} 
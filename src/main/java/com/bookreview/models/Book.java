package com.bookreview.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "books", indexes = {
    @Index(name = "idx_books_title", columnList = "title"),
    @Index(name = "idx_books_isbn", columnList = "isbn"),
    @Index(name = "idx_books_author", columnList = "author_id"),
    @Index(name = "idx_books_avg_rating", columnList = "average_rating"),
    @Index(name = "idx_books_publication_date", columnList = "publication_date"),
    @Index(name = "idx_books_status", columnList = "status"),
    @Index(name = "idx_books_created_at", columnList = "created_at"),
    @Index(name = "idx_books_search", columnList = "search_vector"),
    @Index(name = "idx_books_title_author", columnList = "title, author_id")
})
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"author", "coAuthors", "genres", "reviews"})
@ToString(exclude = {"author", "coAuthors", "genres", "reviews"})
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(length = 500)
    private String subtitle;

    @Column(unique = true, length = 20)
    private String isbn;

    @Column(name = "isbn_13", length = 20)
    private String isbn13;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "publication_date")
    private LocalDate publicationDate;

    @Column(name = "page_count")
    private Integer pageCount;

    @Column(length = 50)
    private String language;

    @Column(length = 200)
    private String publisher;

    @Column(name = "cover_image_url", length = 500)
    private String coverImageUrl;

    @Column(name = "price", precision = 10, scale = 2)
    private java.math.BigDecimal price;

    @Column(length = 10)
    private String currency;

    @Column(name = "search_vector", columnDefinition = "tsvector")
    private String searchVector;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private BookStatus status = BookStatus.PUBLISHED;

    @Column(name = "average_rating", precision = 3)
    private Double averageRating;

    @Column(name = "review_count")
    @Builder.Default
    private Integer reviewCount = 0;

    @Column(name = "total_rating_sum")
    @Builder.Default
    private Long totalRatingSum = 0L;

    @Column(name = "view_count")
    @Builder.Default
    private Long viewCount = 0L;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "is_featured")
    @Builder.Default
    private Boolean isFeatured = false;

    @Column(name = "is_bestseller")
    @Builder.Default
    private Boolean isBestseller = false;

    @Column(name = "is_free")
    @Builder.Default
    private Boolean isFree = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "book_co_authors",
        joinColumns = @JoinColumn(name = "book_id"),
        inverseJoinColumns = @JoinColumn(name = "author_id"),
        indexes = {
            @Index(name = "idx_book_co_authors_book", columnList = "book_id"),
            @Index(name = "idx_book_co_authors_author", columnList = "author_id")
        }
    )
    @Builder.Default
    private Set<User> coAuthors = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "book_genres",
        joinColumns = @JoinColumn(name = "book_id"),
        inverseJoinColumns = @JoinColumn(name = "genre_id"),
        indexes = {
            @Index(name = "idx_book_genres_book", columnList = "book_id"),
            @Index(name = "idx_book_genres_genre", columnList = "genre_id")
        }
    )
    @Builder.Default
    private Set<Genre> genres = new HashSet<>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Review> reviews = new HashSet<>();

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "last_modified_by")
    private String lastModifiedBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    private void validateBook() {
        if (pageCount != null && pageCount <= 0) {
            throw new IllegalArgumentException("Page count must be greater than 0");
        }
        if (price != null && price.compareTo(java.math.BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
    }
}
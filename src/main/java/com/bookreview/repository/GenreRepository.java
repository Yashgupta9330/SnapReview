package com.bookreview.repository;

import com.bookreview.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GenreRepository extends JpaRepository<Genre, Long> {
    Optional<Genre> findByName(String name);
    Optional<Genre> findBySlug(String slug);
    boolean existsByName(String name);
    boolean existsBySlug(String slug);
    List<Genre> findByIsActiveTrue();
    @Query("SELECT COUNT(b) > 0 FROM Book b JOIN b.genres g WHERE g.id = :genreId")
    boolean isGenreUsedByBooks(@Param("genreId") Long genreId);
} 
package com.bookreview.controller;

import com.bookreview.dto.GenreDTO;
import com.bookreview.dto.GenreCreateRequest;
import com.bookreview.entity.Genre;
import com.bookreview.repository.GenreRepository;
import com.bookreview.service.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import jakarta.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

    @GetMapping
    public ResponseEntity<List<GenreDTO>> getAllGenres() {
        return ResponseEntity.ok(genreService.getAllGenres());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getGenre(@PathVariable Long id) {
        Optional<GenreDTO> genre = genreService.getGenre(id);
        if (genre.isPresent()) {
            return ResponseEntity.ok(genre.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Genre not found"));
        }
    }

    @GetMapping("/active")
    public ResponseEntity<List<GenreDTO>> getActiveGenres() {
        List<GenreDTO> genres = genreService.getActiveGenres();
        return ResponseEntity.ok(genres);
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<?> getGenreBySlug(@PathVariable String slug) {
        Optional<GenreDTO> genre = genreService.getGenreBySlug(slug);
        if (genre.isPresent()) {
            return ResponseEntity.ok(genre.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Genre not found"));
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<GenreDTO> createGenre(@Valid @RequestBody GenreCreateRequest request) {
        GenreDTO created = genreService.createGenre(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> updateGenre(@PathVariable Long id, 
                                         @Valid @RequestBody GenreCreateRequest request) {
        Optional<GenreDTO> updated = genreService.updateGenre(id, request);
        if (updated.isPresent()) {
            return ResponseEntity.ok(updated.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Genre not found"));
        }
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deactivateGenre(@PathVariable Long id) {
        if (genreService.deactivateGenre(id)) {
            return ResponseEntity.ok(Map.of("message", "Genre deactivated successfully"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Genre not found"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteGenre(@PathVariable Long id) {
        if (genreService.deleteGenre(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

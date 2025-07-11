package com.bookreview.controller;

import com.bookreview.entity.Genre;
import com.bookreview.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {

    private final GenreRepository genreRepository;

    @GetMapping
    public List<Genre> getAllGenres() {
        return genreRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getGenre(@PathVariable Long id) {
        Optional<Genre> genre = genreRepository.findById(id);
        if (genre.isPresent()) {
            return ResponseEntity.ok(genre.get());
        } else {
            return ResponseEntity.status(404).body(java.util.Map.of("error", "Genre not found"));
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public Genre createGenre(@RequestBody Genre genre) {
        return genreRepository.save(genre);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> updateGenre(@PathVariable Long id, @RequestBody Genre updatedGenre) {
        Optional<Genre> genreOpt = genreRepository.findById(id);
        if (genreOpt.isPresent()) {
            Genre genre = genreOpt.get();
            genre.setName(updatedGenre.getName());
            genre.setSlug(updatedGenre.getSlug());
            genre.setDescription(updatedGenre.getDescription());
            return ResponseEntity.ok(genreRepository.save(genre));
        } else {
            return ResponseEntity.status(404).body(java.util.Map.of("error", "Genre not found"));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteGenre(@PathVariable Long id) {
        genreRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

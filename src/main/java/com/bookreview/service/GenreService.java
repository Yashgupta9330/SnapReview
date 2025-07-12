package com.bookreview.service;

import com.bookreview.entity.Genre;
import com.bookreview.repository.GenreRepository;
import com.bookreview.dto.GenreDTO;
import com.bookreview.dto.GenreCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreRepository genreRepository;

    public GenreDTO createGenre(GenreCreateRequest request) {
        Genre genre = Genre.builder()
                .name(request.getName())
                .slug(request.getSlug())
                .description(request.getDescription())
                .isActive(request.getIsActive())
                .build();
        return toDTO(genreRepository.save(genre));
    }

    public List<GenreDTO> getAllGenres() {
        return genreRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public Optional<GenreDTO> getGenre(Long id) {
        return genreRepository.findById(id).map(this::toDTO);
    }

    public Optional<GenreDTO> updateGenre(Long id, GenreCreateRequest request) {
        return genreRepository.findById(id).map(genre -> {
            genre.setName(request.getName());
            genre.setSlug(request.getSlug());
            genre.setDescription(request.getDescription());
            genre.setIsActive(request.getIsActive());
            return toDTO(genreRepository.save(genre));
        });
    }

    public boolean deleteGenre(Long id) {
        if (genreRepository.existsById(id)) {
            genreRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public List<GenreDTO> getActiveGenres() {
        return genreRepository.findByIsActiveTrue()
                .stream()
                .map(this::toDTO)
                .collect(java.util.stream.Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<GenreDTO> getGenreBySlug(String slug) {
        return genreRepository.findBySlug(slug)
                .map(this::toDTO);
    }

    public boolean deactivateGenre(Long id) {
        return genreRepository.findById(id)
                .map(genre -> {
                    genre.setIsActive(false);
                    genreRepository.save(genre);
                    return true;
                })
                .orElse(false);
    }

    private GenreDTO toDTO(Genre genre) {
        return GenreDTO.builder()
                .id(genre.getId())
                .name(genre.getName())
                .slug(genre.getSlug())
                .description(genre.getDescription())
                .isActive(genre.getIsActive())
                .build();
    }
}

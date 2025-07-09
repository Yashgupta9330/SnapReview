package com.bookreview.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class BookDTO {
    private Long id;
    private String title;
    private String subtitle;
    private String isbn;
    private LocalDate publicationDate;
    private Double averageRating;
    private String authorUsername;
    private List<String> genreNames;
} 
package com.bookreview.dto;

import com.bookreview.entity.BookStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {
    private Long id;
    private String title;
    private String subtitle;
    private String isbn;
    private LocalDate publicationDate;
    private Double averageRating;
    private String description;
    private BookStatus status;
    private String publisher;
    private String authorUsername;
    private List<String> genreNames;
    private List<String> coAuthorUsernames;
}
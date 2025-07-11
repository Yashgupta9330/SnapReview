package com.bookreview.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

@Data
public class BookCreateRequest {
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;
    
    @Size(max = 255, message = "Subtitle must not exceed 255 characters")
    private String subtitle;
    
    @Size(max = 20, message = "ISBN must not exceed 20 characters")
    private String isbn;
    
    private LocalDate publicationDate;
    
    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;
    
    private String status; // Will be converted to BookStatus enum
    
    @Size(max = 255, message = "Publisher must not exceed 255 characters")
    private String publisher;
    
    private List<Long> genreIds;
    private List<Long> coAuthorIds;
}
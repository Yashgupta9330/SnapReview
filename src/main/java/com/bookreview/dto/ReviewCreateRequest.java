package com.bookreview.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
public class ReviewCreateRequest {
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    @Size(max = 2000, message = "Content must not exceed 2000 characters")
    private String content;

    @NotNull(message = "Rating is required")
    private Integer rating;

    @NotNull(message = "Book ID is required")
    private Long bookId;
} 
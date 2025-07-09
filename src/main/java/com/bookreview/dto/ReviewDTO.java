package com.bookreview.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ReviewDTO {
    private Long id;
    private String content;
    private Integer rating;
    private String title;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String username;
    private Long bookId;
} 
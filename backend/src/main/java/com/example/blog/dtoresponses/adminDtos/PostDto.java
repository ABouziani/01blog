package com.example.blog.dtoresponses.adminDtos;
import java.time.LocalDateTime;

public record PostDto(
    Long id,
    String title,
    String description,
    String authorName,
    LocalDateTime createdAt,
    boolean isHidden
) {}
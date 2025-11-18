package com.example.blog.dtoresponses.adminDtos;
import java.time.LocalDateTime;

import com.example.blog.model.Post;

public record PostReportDto(
    Long id,
    Long reporterId,
    String reporterUsername,
    Post reportedPost,
    String reason,
    LocalDateTime createdAt,
    String status
) {}
package com.example.blog.dtoresponses.adminDtos;
import java.time.LocalDateTime;

public record ReportDto(
    Long id,
    Long reporterId,
    String reporterUsername,
    Long reportedUserId,
    String reportedUserUsername,
    Long reportedPostId, // keep null (not used for now)
    String reason,
    LocalDateTime createdAt,
    String status
) {}
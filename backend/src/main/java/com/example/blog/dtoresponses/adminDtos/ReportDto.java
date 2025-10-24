package com.example.blog.dtoresponses.adminDtos;
import java.time.LocalDateTime;

public record ReportDto(
    Long id,
    Long reporterId,
    Long reportedUserId,
    Long reportedPostId, // not used in your model, but Angular expects it — we'll keep null
    String reason,
    LocalDateTime createdAt
) {}
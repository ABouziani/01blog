package com.example.blog.dtoresponses.adminDtos;

import java.time.LocalDateTime;

public record UserDto(
    Long id,
    String username,
    String email,
    boolean isBanned,
    LocalDateTime createdAt,
    boolean isAdmin
) {}
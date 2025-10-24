package com.example.blog.dtoresponses.adminDtos;

public record UserDto(
    Long id,
    String username,
    String email,
    boolean isBanned
) {}
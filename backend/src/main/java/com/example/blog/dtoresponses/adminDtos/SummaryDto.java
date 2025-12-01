package com.example.blog.dtoresponses.adminDtos;

public record SummaryDto(
    Long usersCount,
    Long postsCount,
    Long usersReportsCount,
    Long postsReportsCount
) {}
package com.example.blog.dtoresponses;

public record HomePostDto(
        Long id,
        String title,
        Long creatorId,
        String creatorUsername,
        String media
        ) {

}

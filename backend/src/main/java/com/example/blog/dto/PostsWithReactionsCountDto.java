package com.example.blog.dto;

import java.time.LocalDateTime;

public interface PostsWithReactionsCountDto {
    Long getId();
    String getTitle();
    String getDescription();
    String getMedia();
    LocalDateTime getCreatedAt();
    Long getLikes();
    Long getDislikes();
    Long getCreatorId();
    String getCreatorUsername();
}

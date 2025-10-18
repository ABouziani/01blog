package com.example.blog.dtoresponses;

import java.time.LocalDateTime;

public class PostDto {
    private Long id;
    private String title;
    private String description;
    private String media;
    private LocalDateTime createdAt;
    private UserDto creator; // Include creator (but without posts to avoid cycle)

    // Constructors
    public PostDto() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getMedia() { return media; }
    public void setMedia(String media) { this.media = media; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public UserDto getCreator() { return creator; }
    public void setCreator(UserDto creator) { this.creator = creator; }
}
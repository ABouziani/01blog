package com.example.blog.dtoresponses;

import java.time.LocalDateTime;

public class NotificationResponseDto {

    private Long id;
    private Long postId;
    private String postTitle;
    private Long authorId;
    private String authorUsername;
    private String authorPhoto;
    private LocalDateTime postCreatedAt;
    private boolean unread;

    // Constructors, getters, setters
    public NotificationResponseDto(
            Long id,
            Long postId,
            String postTitle,
            Long authorId,
            String authorUsername,
            String authorPhoto,
            LocalDateTime postCreatedAt,
            boolean unread
    ) {
        this.id = id;
        this.postId = postId;
        this.postTitle = postTitle;
        this.authorId = authorId;
        this.authorUsername = authorUsername;
        this.authorPhoto = authorPhoto;
        this.postCreatedAt = postCreatedAt;
        this.unread = unread;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public Long getPostId() {
        return postId;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public String getAuthorUsername() {
        return authorUsername;
    }

    public String getAuthorPhoto() {
        return authorPhoto;
    }

    public LocalDateTime getPostCreatedAt() {
        return postCreatedAt;
    }

    public boolean isUnread() {
        return unread;
    }
}

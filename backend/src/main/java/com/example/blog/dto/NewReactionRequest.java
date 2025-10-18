package com.example.blog.dto;

import java.time.LocalDateTime;


public class NewReactionRequest {

    public NewReactionRequest() {
    }
    private long postId;
    private String type;
    private LocalDateTime createdAt = LocalDateTime.now();

    public long getPostId() {
        return postId;
    }

    public void setPostId(long postId) {
        this.postId = postId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

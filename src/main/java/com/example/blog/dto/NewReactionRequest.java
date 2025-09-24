package com.example.blog.dto;

import java.time.LocalDateTime;

import com.example.blog.model.Post;
import com.example.blog.model.User;


public class NewReactionRequest {

    public NewReactionRequest() {
    }
    private User user;
    private Post post;
    private String type;
    private LocalDateTime createdAt = LocalDateTime.now();



    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
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

package com.example.blog.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "comments")
public class Comment {

    public Comment() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "post_id", referencedColumnName = "id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Post post;

    @Column(nullable = false)
    @Size(min = 1, max = 400, message = "Comment must be between 1 and 400 characters long")
    private String content;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Transient
    private boolean owner;

    // --- Getters & Setters ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setOwner(boolean owner) {
        this.owner = owner;
    }

    public boolean getOwner() {
        return this.owner;
    }
}

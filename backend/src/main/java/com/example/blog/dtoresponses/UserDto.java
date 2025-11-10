package com.example.blog.dtoresponses;

import java.time.LocalDateTime;
import java.util.List;

import com.example.blog.model.User;

public class UserDto {

    private Long id;
    private String username;
    private String email;
    private String bio;
    private String photo;
    private LocalDateTime createdAt;
    private String role;
    private List<PostDto> posts;
    private boolean isAlreadyReported;
    private boolean isAlreadyFollowed;
    private boolean owner;
    private List<User> followers;
    private List<User> following;

    // Constructors
    public UserDto() {
    }

    public boolean isAlreadyReported() {
        return isAlreadyReported;
    }

    public void setIsAlreadyReported(boolean isAlreadyReported) {
        this.isAlreadyReported = isAlreadyReported;
    }

    public boolean isAlreadyFollowed() {
        return isAlreadyFollowed;
    }

    public void setIsAlreadyFollowed(boolean isAlreadyFollowed) {
        this.isAlreadyFollowed = isAlreadyFollowed;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<PostDto> getPosts() {
        return posts;
    }

    public void setPosts(List<PostDto> posts) {
        this.posts = posts;
    }

    public void setOwner(boolean owner) {
        this.owner = owner;
    }

    public boolean getOwner() {
        return this.owner;
    }

    public List<User> getFollowers() {
        return followers;
    }

    public void setFollowers(List<User> followers) {
        this.followers = followers;
    }

    public List<User> getFollowing() {
        return following;
    }

    public void setFollowing(List<User> following) {
        this.following = following;
    }
}

package com.example.blog.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "users")
public class User {

    public User() {
        this.posts = new ArrayList<>();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "Username cannot be blank")
    @Size(min = 3, message = "Username must be at least 3 characters long")
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private boolean isBanned = false;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false)
    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    @Column(length = 500)
    private String bio;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false)
    private String role = "USER"; // default role

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime lastnotifcheck = LocalDateTime.now();

    private String photo; // can store file path or URL

    @JsonIgnore
    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Post> posts;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private RefreshToken refreshToken;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reaction> reactions = new ArrayList<>();

    // --- FOLLOWERS / FOLLOWING ---
    @OneToMany(mappedBy = "followed", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<UserFollow> followers = new ArrayList<>();

    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<UserFollow> following = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> notifications = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CustomUnreadNotif> unreadNotifications = new HashSet<>();

    @Transient
    private boolean isAlreadyReported;

    @Transient
    private boolean owner;

    @Transient
    private boolean isAlreadyFollowed;

    // --- Helper Methods for Following ---
    public void follow(User userToFollow) {
        // Check if already following
        boolean alreadyFollowing = this.following.stream()
                .anyMatch(uf -> uf.getFollowed().equals(userToFollow));
        if (alreadyFollowing) {
            return;
        }

        UserFollow uf = new UserFollow();
        uf.setFollower(this);             // me
        uf.setFollowed(userToFollow);     // user being followed
        uf.setFollowedAt(LocalDateTime.now());

        this.following.add(uf);
        userToFollow.getFollowers().add(uf); // keep both sides in sync
    }

    public void unfollow(User userToUnfollow) {
        // Find the UserFollow object
        UserFollow uf = this.following.stream()
                .filter(f -> f.getFollowed().equals(userToUnfollow))
                .findFirst()
                .orElse(null);

        if (uf != null) {
            this.following.remove(uf);        // remove from my following list
            userToUnfollow.getFollowers().remove(uf); // remove from their followers list
        }
    }

    // --- Other Helper Methods ---
    public void addReaction(Reaction reaction) {
        reactions.add(reaction);
        reaction.setUser(this);
    }

    public void removeReaction(Reaction reaction) {
        reactions.remove(reaction);
        reaction.setUser(null);
    }

    public void addPost(Post post) {
        posts.add(post);
        post.setCreator(this);
    }

    public void removePost(Post post) {
        posts.remove(post);
        post.setCreator(null);
    }

    // --- Getters & Setters ---
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastNotifCheck() {
        return lastnotifcheck;
    }

    public void setLastNotifCheck(LocalDateTime lastnotifcheck) {
        this.lastnotifcheck = lastnotifcheck;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public List<Reaction> getReactions() {
        return reactions;
    }

    public void setReactions(List<Reaction> reactions) {
        this.reactions = reactions;
    }

    public List<UserFollow> getFollowers() {
        return followers;
    }

    public void setFollowers(List<UserFollow> followers) {
        this.followers = followers;
    }

    public List<UserFollow> getFollowing() {
        return following;
    }

    public void setFollowing(List<UserFollow> following) {
        this.following = following;
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

    public boolean isBanned() {
        return this.isBanned;
    }

    public void setBanned(boolean isBanned) {
        this.isBanned = isBanned;
    }

    public void setOwner(boolean owner) {
        this.owner = owner;
    }

    public boolean getOwner() {
        return this.owner;
    }
}

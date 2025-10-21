package com.example.blog.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "users")
public class User {

    public User() {
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

    private String photo; // can store file path or URL

    @JsonIgnore
    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reaction> reactions = new ArrayList<>();

    // --- FOLLOWERS / FOLLOWING ---
    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "user_followers",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "follower_id")
    )
    private List<User> followers = new ArrayList<>();

    @JsonIgnore
    @ManyToMany(mappedBy = "followers")
    private List<User> following = new ArrayList<>();

    @Transient
    private boolean isAlreadyReported;

    @Transient
    private boolean isAlreadyFollowed;

    @ElementCollection
    @MapKeyColumn(name = "author_id")          // key column
    @Column(name = "post_created_at")          // value column
    @CollectionTable(
            name = "user_unread_notifs",
            joinColumns = @JoinColumn(name = "user_id")
    )
    private Map<Long, LocalDateTime> unreadNotifs = new HashMap<>();

    @ElementCollection
    @MapKeyColumn(name = "author_id")
    @Column(name = "post_created_at")
    @CollectionTable(
            name = "user_read_notifs",
            joinColumns = @JoinColumn(name = "user_id")
    )
    private final Map<Long, LocalDateTime> readNotifs = new HashMap<>();

    // --- Helper Methods for Following ---
    public void follow(User userToFollow) {
        if (!following.contains(userToFollow)) {
            following.add(userToFollow);
            userToFollow.getFollowers().add(this);
        }
    }

    //notifs helper methods
    public void addUnreadNotification(Long authorId, LocalDateTime postTime) {
        // Only add if newer than existing (optional but safe)
        LocalDateTime existing = unreadNotifs.get(authorId);
        if (existing == null || postTime.isAfter(existing)) {
            unreadNotifs.put(authorId, postTime);
        }
    }

    public void markAuthorNotificationsAsRead(Long authorId) {
        LocalDateTime time = unreadNotifs.remove(authorId);
        if (time != null) {
            readNotifs.put(authorId, time);
        }
    }

// Optional: mark all as read
    public void markAllNotificationsAsRead() {
        readNotifs.putAll(unreadNotifs);
        unreadNotifs.clear();
    }

    public void unfollow(User userToUnfollow) {
        if (following.contains(userToUnfollow)) {
            following.remove(userToUnfollow);
            userToUnfollow.getFollowers().remove(this);
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

    public Map<Long, LocalDateTime> getReadNotifs() {
        return readNotifs;
    }

    public Map<Long, LocalDateTime> getUnReadNotifs() {
        return unreadNotifs;
    }
}

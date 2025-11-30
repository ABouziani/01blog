package com.example.blog.dtoresponses;

import java.time.LocalDateTime;
import java.util.List;

import com.example.blog.model.Post;
import com.example.blog.model.User;

public interface UserSearchDto {
    Long getId();
    String getUsername();
    String getPhoto(); 
    String getBio();
    LocalDateTime getCreatedAt();   
    Long getFollowersCount();
    Long getFollowingCount();
    Long getPostsCount();
}
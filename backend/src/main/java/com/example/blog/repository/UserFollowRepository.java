package com.example.blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.blog.model.User;
import com.example.blog.model.UserFollow;

public interface UserFollowRepository extends JpaRepository<UserFollow, Long> {
    boolean existsByFollowerAndFollowed(User follower, User followed);
    void deleteByFollowerIdAndFollowedId(Long followerId, Long followedId);
}

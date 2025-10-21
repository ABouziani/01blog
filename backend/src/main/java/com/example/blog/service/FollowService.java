// FollowService.java
package com.example.blog.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.blog.model.User;
import com.example.blog.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class FollowService {

    private final UserRepository userRepository;

    public FollowService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void followUser(Long followerId, Long followedId) {
        if (followerId.equals(followedId)) {
            throw new IllegalArgumentException("You cannot follow yourself.");
        }

        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new EntityNotFoundException("Follower user not found"));
        User followed = userRepository.findById(followedId)
                .orElseThrow(() -> new EntityNotFoundException("User to follow not found"));

        // Avoid duplicate follows
        if (!follower.getFollowing().contains(followed)) {
            follower.follow(followed); // This updates both sides
            userRepository.save(follower);
            // Note: followed is automatically updated via relationship, but saving follower is enough
        }
    }

    public void unfollowUser(Long followerId, Long followedId) {
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new EntityNotFoundException("Follower user not found"));
        User followed = userRepository.findById(followedId)
                .orElseThrow(() -> new EntityNotFoundException("User to unfollow not found"));

        if (follower.getFollowing().contains(followed)) {
            follower.unfollow(followed);
            userRepository.save(follower);
        }
    }

    public boolean isFollowing(Long followerId, Long followedId) {
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new EntityNotFoundException("Follower not found"));
        User followed = userRepository.findById(followedId)
                .orElseThrow(() -> new EntityNotFoundException("Followed user not found"));

        return follower.getFollowing().contains(followed);
    }
}
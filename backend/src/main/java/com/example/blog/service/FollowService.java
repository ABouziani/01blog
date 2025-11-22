// FollowService.java
package com.example.blog.service;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.blog.model.User;
import com.example.blog.repository.UserRepository;

@Service
@Transactional
public class FollowService {

    private final UserRepository userRepository;

    public FollowService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ResponseEntity<?> followUser(Long followerId, Long followedId) {
        if (followerId.equals(followedId)) {
            return new ResponseEntity<>("You cannot follow yourself.", HttpStatus.BAD_REQUEST);
        }

        Optional<User> followerOpt = userRepository.findById(followerId);
        if (followerOpt.isEmpty()) {
            return new ResponseEntity<>("Follower user not found", HttpStatus.BAD_REQUEST);
        }
        User follower = followerOpt.get();

        Optional<User> followedOpt = userRepository.findById(followedId);
        if (followedOpt.isEmpty()) {
            return new ResponseEntity<>("Follower user not found", HttpStatus.BAD_REQUEST);
        }
        User followed = followedOpt.get();

        if (!follower.getFollowing().contains(followed)) {
            follower.follow(followed);
            userRepository.save(follower);
        }
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<?> unfollowUser(Long followerId, Long followedId) {
        Optional<User> followerOpt = userRepository.findById(followerId);
        if (followerOpt.isEmpty()) {
            return new ResponseEntity<>("Follower user not found", HttpStatus.BAD_REQUEST);
        }
        User follower = followerOpt.get();

        Optional<User> followedOpt = userRepository.findById(followedId);
        if (followedOpt.isEmpty()) {
            return new ResponseEntity<>("Follower user not found", HttpStatus.BAD_REQUEST);
        }
        User followed = followedOpt.get();

        if (follower.getFollowing().contains(followed)) {
            follower.unfollow(followed);
            userRepository.save(follower);
        }
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<?> isFollowing(Long followerId, Long followedId) {
        Optional<User> followerOpt = userRepository.findById(followerId);
        if (followerOpt.isEmpty()) {
            return new ResponseEntity<>("Follower user not found", HttpStatus.BAD_REQUEST);
        }
        User follower = followerOpt.get();

        Optional<User> followedOpt = userRepository.findById(followedId);
        if (followedOpt.isEmpty()) {
            return new ResponseEntity<>("Follower user not found", HttpStatus.BAD_REQUEST);
        }
        User followed = followedOpt.get();

        return ResponseEntity.ok(follower.getFollowing().contains(followed));
    }
}

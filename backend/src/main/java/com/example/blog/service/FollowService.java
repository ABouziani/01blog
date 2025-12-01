// FollowService.java
package com.example.blog.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.blog.model.User;
import com.example.blog.model.UserFollow;
import com.example.blog.repository.UserFollowRepository;
import com.example.blog.repository.UserRepository;

@Service
@Transactional
public class FollowService {

    private final UserRepository userRepository;
    private final UserFollowRepository userFollowRepository;

    public FollowService(UserRepository userRepository, UserFollowRepository userFollowRepository) {
        this.userRepository = userRepository;
        this.userFollowRepository = userFollowRepository;

    }

    public ResponseEntity<?> followUser(Long followerId, Long followedId) {
        if (followerId.equals(followedId)) {
            return new ResponseEntity<>("You cannot follow yourself.", HttpStatus.BAD_REQUEST);
        }

        User follower = userRepository.findById(followerId)
                .orElse(null);
        if (follower == null) {
            return new ResponseEntity<>("Follower user not found", HttpStatus.BAD_REQUEST);
        }

        User followed = userRepository.findById(followedId)
                .orElse(null);
        if (followed == null) {
            return new ResponseEntity<>("Followed user not found", HttpStatus.BAD_REQUEST);
        }

        /// Prevent duplicate rows
    if (userFollowRepository.existsByFollowerAndFollowed(follower, followed)) {
            return ResponseEntity.ok("Already following");
        }

        UserFollow uf = new UserFollow();
        uf.setFollower(follower);  
        uf.setFollowed(followed); 

        userFollowRepository.save(uf);

        return ResponseEntity.ok().build();
    }

    public ResponseEntity<?> unfollowUser(Long followerId, Long followedId) {
        User follower = userRepository.findById(followerId)
                .orElse(null);
        if (follower == null) {
            return new ResponseEntity<>("Follower user not found", HttpStatus.BAD_REQUEST);
        }

        User followed = userRepository.findById(followedId)
                .orElse(null);
        if (followed == null) {
            return new ResponseEntity<>("Followed user not found", HttpStatus.BAD_REQUEST);
        }

        userFollowRepository.deleteByFollowerIdAndFollowedId(followerId, followedId);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<?> isFollowing(Long followerId, Long followedId) {
        User follower = userRepository.findById(followerId)
                .orElse(null);
        if (follower == null) {
            return new ResponseEntity<>("Follower user not found", HttpStatus.BAD_REQUEST);
        }

        User followed = userRepository.findById(followedId)
                .orElse(null);
        if (followed == null) {
            return new ResponseEntity<>("Followed user not found", HttpStatus.BAD_REQUEST);
        }

        // Check if follower has a UserFollow entry pointing to the followed user
        boolean isFollowing = follower.getFollowing().stream()
                .anyMatch(uf -> uf.getFollowed().equals(followed));

        return ResponseEntity.ok(isFollowing);
    }

}

// FollowController.java
package com.example.blog.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.blog.service.FollowService;
import com.example.blog.service.UserDetailsImpl;

import jakarta.validation.constraints.Positive;

@RestController
@RequestMapping("/api/follow")
public class FollowController {

    private final FollowService followService;

    public FollowController(FollowService followService) {
        this.followService = followService;
    }

    @PostMapping("/{userId}")
    public ResponseEntity<?> followUser(@PathVariable @Positive Long userId, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long loggedUserId = userDetails.getId();
        followService.followUser(loggedUserId, userId);
        return ResponseEntity.ok(Map.of("message","You successfully followed this user!"));
    }

    
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> unfollowUser(@PathVariable @Positive Long userId,Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long loggedUserId = userDetails.getId();
        followService.unfollowUser(loggedUserId, userId);
        return ResponseEntity.ok(Map.of("message","You successfully unfollowed this user!"));
    }

    
    @GetMapping("/check/{userId}")
    public ResponseEntity<?> isFollowing(@PathVariable @Positive Long userId, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long loggedUserId = userDetails.getId();
        return followService.isFollowing(loggedUserId, userId);
    }
}
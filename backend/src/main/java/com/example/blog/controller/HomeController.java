package com.example.blog.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.blog.dtoresponses.ActiveUsersDto;
import com.example.blog.dtoresponses.HomePostDto;
import com.example.blog.service.PostService;
import com.example.blog.service.UserDetailsImpl;
import com.example.blog.service.UserService;

@RestController
@RequestMapping("/api/home")
public class HomeController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<?> getallPosts(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long loggedUserId = userDetails.getId();
        List<HomePostDto> homePosts = postService.getRecentPostsForHome(loggedUserId,6);
        List<ActiveUsersDto> homeUsers = userService.getActiveUsersForHome(5);
        List<HomePostDto> topPosts = postService.getTopPostsForHome(5);
        return ResponseEntity.ok(Map.of(
        "recentPosts", homePosts,
        "activeUsers", homeUsers,
        "topPosts", topPosts
    ));
    }

}

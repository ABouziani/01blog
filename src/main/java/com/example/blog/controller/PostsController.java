package com.example.blog.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.blog.service.UserDetailsImpl;

@RestController
@RequestMapping("/api/")
public class PostsController {

    @GetMapping("/posts")
    public String getPosts() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        Long userId = userDetails.getId();
        System.out.println(userId);
        return "Okk";
    }

    @GetMapping("/test-principal")
    public String test(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        System.out.println("Principal type: " + principal.getClass().getName());

        if (principal instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) principal;
            return "User ID: " + userDetails.getId();
        } else {
            return "Principal is: " + principal.toString(); // Probably prints username as String
        }
    }
}

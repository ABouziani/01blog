package com.example.blog.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.blog.dto.NewPostRequest;
import com.example.blog.model.Post;
import com.example.blog.service.PostService;
import com.example.blog.service.UserDetailsImpl;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class PostsController {

    @Autowired
    private PostService postService;

    @GetMapping("/posts/{userid}")
    public List<Post> getPosts(@PathVariable Long userid) {
        return postService.getPostsByUserId(userid).orElse(List.of());
    }

    @PostMapping("/posts/create")
    public ResponseEntity<Post> createPosts(@Valid @RequestBody NewPostRequest newPostRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        Long loggedUserId = userDetails.getId();
        Post savedPost = postService.createPost(newPostRequest, loggedUserId);
        return ResponseEntity.ok(savedPost); // returns 200 + JSON of saved user
    }

    @PutMapping("/posts/update/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable Long id,@Valid @RequestBody NewPostRequest updateRequest,Authentication authentication) {

        // Get current user ID
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long requesterId = userDetails.getId();

        // Update post (service checks ownership)
        Post updatedPost = postService.updatePost(id, updateRequest, requesterId);

        return ResponseEntity.ok(updatedPost);
    }
}

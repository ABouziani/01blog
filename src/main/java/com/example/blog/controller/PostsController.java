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
@RequestMapping("/api/posts")
public class PostsController {

    @Autowired
    private PostService postService;

    @GetMapping("/getuserposts/{userid}")
    public List<Post> getPosts(@PathVariable Long userid) {
        return postService.getPostsByUserId(userid).orElse(List.of());
    }

    @GetMapping("/getpost/{postid}")
    public Post getPost(@PathVariable Long postid) {
        return postService.getPostById(postid).orElse(null);
    }

    @PostMapping("/create")
    public ResponseEntity<Post> createPosts(@Valid @RequestBody NewPostRequest newPostRequest) {
        System.out.println("*************************");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        System.out.println(userDetails);
        Long loggedUserId = userDetails.getId();
        Post savedPost = postService.createPost(newPostRequest, loggedUserId);
        return ResponseEntity.ok(savedPost); // returns 200 + JSON of saved user
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable Long id,@Valid @RequestBody NewPostRequest updateRequest,Authentication authentication) {

        // Get current user ID
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long requesterId = userDetails.getId();

        // Update post (service checks ownership)
        Post updatedPost = postService.updatePost(id, updateRequest, requesterId);

        return ResponseEntity.ok(updatedPost);
    }

    @PutMapping("/delete/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id, Authentication authentication) {

        // Get current user ID
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long requesterId = userDetails.getId();

        // Update post (service checks ownership)
        postService.deletePost(id, requesterId);

        return ResponseEntity.ok("Post deleted successfullly!");
    }
}

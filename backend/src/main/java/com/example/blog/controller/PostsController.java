package com.example.blog.controller;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.blog.dto.PostsWithReactionsCountDto;
import com.example.blog.exceptions.ResourceNotFoundException;
import com.example.blog.model.Post;
import com.example.blog.model.User;
import com.example.blog.repository.UserRepository;
import com.example.blog.service.NotificationService;
import com.example.blog.service.PostService;
import com.example.blog.service.UserDetailsImpl;
import com.example.blog.service.UserService;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@RestController
@RequestMapping("/api/posts")
public class PostsController {

    @Autowired
    private PostService postService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/getuserposts/{userid}")
    public List<Post> getPosts(@PathVariable Long userid,
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = "6") int pageSize) {
        return postService.getPostsByUserId(userid, lastId, pageSize);
    }

    @GetMapping("/getallposts")
    public List<PostsWithReactionsCountDto> getAllPosts(
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = "6") int pageSize) {

        return postService.getAllPosts(lastId, pageSize);
    }

    @GetMapping("/getpost/{postid}")
    public ResponseEntity<?> getPost(@PathVariable Long postid, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long loggedUserId = userDetails.getId();

        return postService.getPost(postid, loggedUserId);
    }

    // ***************

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Post> createPost(
            @RequestParam("title") @NotBlank @Size(min = 6, max = 100) String title,
            @RequestParam("description") @NotBlank @Size(min = 20) String description,
            @RequestParam(value = "media", required = false) MultipartFile media,
            Authentication authentication
    ) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long loggedUserId = userDetails.getId();

        User creator = userService.findById(loggedUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post savedPost = postService.createPost(title, description, media, creator); // update service to accept MultipartFile

        //Notify Followers
        notificationService.createNotificationForNewPost(savedPost);

        return ResponseEntity.ok(savedPost);
    }

    @PutMapping(value = "/edit/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Post> updatePost(
            @PathVariable Long id,
            @RequestParam("title") @NotBlank @Size(min = 3, max = 100) String title,
            @RequestParam("description") @NotBlank @Size(min = 10, max = 1000) String description,
            @RequestParam(value = "media", required = false) MultipartFile media,
            Authentication authentication
    ) {

        // Get current user ID
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long requesterId = userDetails.getId();

        // Update post (service checks ownership)
        Post updatedPost = postService.updatePost(id, title, description, media, requesterId);

        User loggedUser = userRepository.findById(requesterId)
                .orElseThrow(() -> new ResourceNotFoundException("Bad request"));
        if (Objects.equals(updatedPost.getCreator().getId(), requesterId) || loggedUser.getRole().equals("ADMIN")) {
            updatedPost.setIsOwner(true);
        }

        return ResponseEntity.ok(updatedPost);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id, Authentication authentication) {

        // Get current user ID
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long requesterId = userDetails.getId();

        // Update post (service checks ownership)
        postService.deletePost(id, requesterId);

        return ResponseEntity.ok(Map.of("message", "Post deleted successfully!"));
    }

}

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.blog.dto.NewPostRequest;
import com.example.blog.dto.PostsWithReactionsCountDto;
import com.example.blog.model.Post;
import com.example.blog.service.PostService;
import com.example.blog.service.UserDetailsImpl;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@RestController
@RequestMapping("/api/posts")
public class PostsController {

    @Autowired
    private PostService postService;

    @GetMapping("/getuserposts/{userid}")
    public List<Post> getPosts(@PathVariable Long userid) {
        return postService.getPostsByUserId(userid);
    }

    @GetMapping("/getallposts")
    public List<PostsWithReactionsCountDto> getallPosts() {
        return postService.getAllPosts();
    }

    @GetMapping("/getpost/{postid}")
    public Post getPost(@PathVariable Long postid, Authentication authentication) {
        Post post = postService.getPostById(postid).orElse(null);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long loggedUserId = userDetails.getId();
        if (Objects.equals(post.getCreator().getId(), loggedUserId)) {
            post.setIsOwner(true);
        }
        return post;
    }

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Post> createPost(
            @RequestParam("title") @NotBlank @Size(min = 3, max = 100) String title,
            @RequestParam("description") @NotBlank @Size(min = 10, max = 1000) String description,
            @RequestParam(value = "media", required = false) MultipartFile media,
            Authentication authentication
    ) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long loggedUserId = userDetails.getId();

        Post savedPost = postService.createPost(title, description, media, loggedUserId); // update service to accept MultipartFile
        return ResponseEntity.ok(savedPost);
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable Long id, @Valid @RequestBody NewPostRequest updateRequest, Authentication authentication) {

        // Get current user ID
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long requesterId = userDetails.getId();

        // Update post (service checks ownership)
        Post updatedPost = postService.updatePost(id, updateRequest, requesterId);

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

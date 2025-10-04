package com.example.blog.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.blog.dto.NewPostRequest;
import com.example.blog.exceptions.ResourceNotFoundException;
import com.example.blog.model.Post;
import com.example.blog.model.User;
import com.example.blog.repository.PostRepository;

@Service
public class PostService {

    private static final Logger logger = LoggerFactory.getLogger(PostService.class);

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserService userService;

    public Optional<List<Post>> getPostsByUserId(Long userId) {
        return postRepository.findByCreatorId(userId);
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public Optional<Post> getPostById(Long postId) {
        return postRepository.findById(postId);
    }

    public Post createPost(NewPostRequest newPostRequest, Long creatorId) {
        // 1. Map newPostRequest to Entity
        Post post = new Post();
        post.setTitle(newPostRequest.getTitle());
        post.setDescription(newPostRequest.getDescription());
        post.setMedia(newPostRequest.getMedia());

        // 2. Set creator — fetched by ID (from security context usually)
        User creator = userService.findById(creatorId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        post.setCreator(creator);
        post.setCreatedAt(LocalDateTime.now());

        // 3. Save and return
        return postRepository.save(post);
    }

    public Post updatePost(Long postId, NewPostRequest updateRequest, Long requesterId) {
        // 1. Find post
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        // 2. Check ownership

        if (!post.getCreator().getId().equals(requesterId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only modify your own posts");
        }

        // 3. Update fields
        post.setTitle(updateRequest.getTitle());
        post.setDescription(updateRequest.getDescription());
        post.setMedia(updateRequest.getMedia());

        // 4. Log current createdAt (debug)
        logger.info("Updating post ID: {}, current createdAt: {}", postId, post.getCreatedAt());

        try {
            return postRepository.save(post);
        } catch (Exception e) {
            logger.error("Error saving post: ", e);

            // 👇 Unwrap and log root cause
            Throwable cause = e;
            while (cause.getCause() != null) {
                cause = cause.getCause();
            }
            logger.error("Root cause: {}", cause.getMessage());

            throw e;
        }
    }

    public void deletePost(Long postId, Long requesterId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        if (!post.getCreator().getRole().equals("ADMIN")) {
            if (!post.getCreator().getId().equals(requesterId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only delete your own posts");
            }
        }

        postRepository.deleteById(postId);
    }
}

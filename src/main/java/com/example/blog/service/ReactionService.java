package com.example.blog.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.blog.dto.NewPostRequest;
import com.example.blog.dto.NewReactionRequest;
import com.example.blog.exceptions.ResourceNotFoundException;
import com.example.blog.model.Post;
import com.example.blog.model.Reaction;
import com.example.blog.model.User;
import com.example.blog.repository.PostRepository;
import com.example.blog.repository.ReactionRepository;

@Service
public class ReactionService {

    private static final Logger logger = LoggerFactory.getLogger(PostService.class);

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ReactionRepository reactionRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    public Optional<List<Reaction>> getReactionsByUserId(Long userId) {
        return reactionRepository.findByUserId(userId);
    }

    public Optional<List<Reaction>> getReactionsByPostId(Long postId) {
        return reactionRepository.findByPostId(postId);
    }

    public Reaction addReaction(NewReactionRequest newReactionRequest, Long creatorId) {

        User reactor = userService.findById(creatorId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = postService.getPostById(newReactionRequest.getPostId())
                .orElseThrow(() -> new RuntimeException("post not found"));

        String newType = newReactionRequest.getType().toLowerCase();
        // Check if reaction already exists
        Optional<Reaction> existingReactionOpt = reactionRepository.findByUserIdAndPostId(creatorId, newReactionRequest.getPostId());

        if (existingReactionOpt.isPresent()) {
            Reaction existing = existingReactionOpt.get();
            String existingType = existing.getType().toLowerCase();

            if (existingType.equals(newType)) {
                // Same reaction: remove it (toggle off)
                reactionRepository.delete(existing);
                return null; // or throw exception, or return a "deleted" response
            } else {
                // Different reaction: update type
                existing.setType(newType);
                existing.setCreatedAt(LocalDateTime.now()); // optional: update timestamp
                return reactionRepository.save(existing);
            }
        } else {
            // No existing reaction: create new one
            Reaction newReaction = new Reaction();
            newReaction.setUser(reactor);
            newReaction.setPost(post);
            newReaction.setType(newType);
            newReaction.setCreatedAt(LocalDateTime.now());
            return reactionRepository.save(newReaction);
        }
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

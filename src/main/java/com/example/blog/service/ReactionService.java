package com.example.blog.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.blog.dto.NewReactionRequest;
import com.example.blog.model.Post;
import com.example.blog.model.Reaction;
import com.example.blog.model.User;
import com.example.blog.repository.ReactionRepository;

@Service
public class ReactionService {

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
        Post post = postService.getPostById(postId)
                .orElseThrow(() -> new RuntimeException("post not found"));
        return reactionRepository.findByPostId(postId);
    }

    public Reaction addorupdateReaction(NewReactionRequest newReactionRequest, Long creatorId) {

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
}

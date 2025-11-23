package com.example.blog.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    public List<Reaction> getAll() {
        return reactionRepository.findAll();
    }

    public Optional<List<Reaction>> getReactionsByPostId(Long postId) {
        Post post = postService.getPostById(postId)
                .orElseThrow(() -> new RuntimeException("post not found"));
        return reactionRepository.findByPostId(postId);
    }

    public ResponseEntity<?> addorupdateReaction(NewReactionRequest newReactionRequest, Long creatorId) {
        Optional<User> userOpt = userService.findById(creatorId);
        if (userOpt.isEmpty()) {
            return new ResponseEntity<>("User not found", HttpStatus.BAD_REQUEST);
        }
        User reactor = userOpt.get();

        Optional<Post> postOpt = postService.getPostById(newReactionRequest.getPostId());
        if (postOpt.isEmpty()) {
            return new ResponseEntity<>("post not found", HttpStatus.BAD_REQUEST);
        }
        Post post = postOpt.get();

        String newType = newReactionRequest.getType().toLowerCase();
        // Check if reaction already exists
        Optional<Reaction> existingReactionOpt = reactionRepository.findByUserIdAndPostId(creatorId, newReactionRequest.getPostId());

        if (existingReactionOpt.isPresent()) {
            Reaction existing = existingReactionOpt.get();
            String existingType = existing.getType().toLowerCase();

            if (existingType.equals(newType)) {
                // Same reaction: remove it (toggle off)
                reactionRepository.delete(existing);
                return ResponseEntity.ok(existing); 
            } else {
                existing.setType(newType);
                existing.setCreatedAt(LocalDateTime.now()); 
                reactionRepository.save(existing);
                return ResponseEntity.ok().build();
            }
        } else {
            Reaction newReaction = new Reaction();
            newReaction.setUser(reactor);
            newReaction.setPost(post);
            newReaction.setType(newType);
            newReaction.setCreatedAt(LocalDateTime.now());
            reactionRepository.save(newReaction);
            return ResponseEntity.ok().build();
        }
    }
}

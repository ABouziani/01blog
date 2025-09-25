package com.example.blog.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.blog.dto.NewCommentRequest;
import com.example.blog.dto.NewReactionRequest;
import com.example.blog.model.Comment;
import com.example.blog.model.Post;
import com.example.blog.model.Reaction;
import com.example.blog.model.User;
import com.example.blog.repository.CommentRepository;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    public Optional<List<Comment>> getCommentsByUserId(Long userId) {
        return commentRepository.findByUserId(userId);
    }

    public Optional<List<Comment>> getCommentsByPostId(Long postId) {
        return commentRepository.findByPostId(postId);
    }

    public Comment addComment(NewCommentRequest newCommentRequest, Long creatorId) {

        User commentor = userService.findById(creatorId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = postService.getPostById(newCommentRequest.getPostId())
                .orElseThrow(() -> new RuntimeException("post not found"));

        Comment newComment = new Comment();
        newComment.setUser(commentor);
        newComment.setPost(post);
        newComment.setContent(newCommentRequest.getContent());
        newComment.setCreatedAt(LocalDateTime.now());
        return commentRepository.save(newComment);
    }
}

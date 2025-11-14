package com.example.blog.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.blog.dto.NewCommentRequest;
import com.example.blog.model.Comment;
import com.example.blog.model.Post;
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

    public ResponseEntity<?> deleteComment(Long commentId, Long loggedUserId) {
        Optional<User> loggedUserOpt = userService.findById(loggedUserId);
        Optional<Comment> commentOpt = commentRepository.findById(commentId);

        if (commentOpt.isPresent() && loggedUserOpt.isPresent()) {
            if (!Objects.equals(commentOpt.get().getUser().getId(), loggedUserOpt.get().getId())) {
                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body("You can delete only your comments");
            }
            commentRepository.delete(commentOpt.get());
        }
        return ResponseEntity.ok().build();
    }

    public List<Comment> getCommentsByPostId(Long postid, Long lastId, int pageSize) {
        Pageable limit = PageRequest.of(0, pageSize, Sort.by(Sort.Direction.DESC, "id"));
        if (lastId == 0) {
            return commentRepository.getCommentsByPostId(postid, null, limit);
        } else {
            return commentRepository.getCommentsByPostId(postid, lastId, limit);
        }
    }
}

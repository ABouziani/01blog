package com.example.blog.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.blog.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Override
    Optional<Comment> findById(Long id);
    Optional<List<Comment>> findByPostId(Long commentId);
    Optional<List<Comment>> findByUserId(Long userId);
    Optional<Comment> findByUserIdAndPostId(Long userId, Long postId);
}

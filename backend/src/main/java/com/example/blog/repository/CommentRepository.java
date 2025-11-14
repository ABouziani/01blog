package com.example.blog.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.blog.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Override
    Optional<Comment> findById(Long id);
    Optional<List<Comment>> findByPostId(Long commentId);
    Optional<List<Comment>> findByUserId(Long userId);
    Optional<Comment> findByUserIdAndPostId(Long userId, Long postId);

    @Query("""
    SELECT c FROM Comment c 
        WHERE c.post.id = :postid
        AND (:lastId IS NULL OR c.id < :lastId)
    """)
    List<Comment> getCommentsByPostId(@Param("postid") Long postid,@Param("lastId") Long lastId,Pageable pageable);
}

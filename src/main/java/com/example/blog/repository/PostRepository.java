package com.example.blog.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.blog.model.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    // Fetch all posts with their reactions
    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.reactions")
    List<Post> findAllWithReactions();
    
    // Fetch posts by creator ID with reactions
    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.reactions WHERE p.creator.id = :userId")
    List<Post> findByCreatorIdWithReactions(@Param("userId") Long userId);
    
    // Fetch single post by ID with reactions
    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.reactions WHERE p.id = :id")
    Optional<Post> findByIdWithReactions(@Param("id") Long id);
    
    // Your existing methods (keep these)
    Optional<List<Post>> findByCreatorId(Long userId);
    
    @Override
    Optional<Post> findById(Long id);

    @Override
    void deleteById(Long id);
}
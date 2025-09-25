package com.example.blog.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.blog.model.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    // You can add custom query methods here if needed, for example:
    // Example: Find posts by creator ID
    Optional<List<Post>> findByCreatorId(Long userId);
    
    @Override
    Optional<Post> findById(Long Id);

    // Example: Find posts by title containing a keyword
    // List<Post> findByTitleContainingIgnoreCase(String keyword);
    @Override
    void deleteById(Long userId);
}

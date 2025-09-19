package com.example.blog.repository;

import java.util.List;
import java.util.Optional;

import com.example.blog.model.Post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    // You can add custom query methods here if needed, for example:

    // Example: Find posts by creator ID
    Optional<List<Post>> findByCreatorId(Long userId);

    // Example: Find posts by title containing a keyword
    // List<Post> findByTitleContainingIgnoreCase(String keyword);

}
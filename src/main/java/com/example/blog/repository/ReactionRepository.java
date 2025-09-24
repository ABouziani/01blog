package com.example.blog.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.blog.model.Reaction;

public interface ReactionRepository extends JpaRepository<Reaction, Long> {
    @Override
    Optional<Reaction> findById(Long id);
    Optional<List<Reaction>> findByPostId(Long postId);
    Optional<List<Reaction>> findByUserId(Long postId);
}

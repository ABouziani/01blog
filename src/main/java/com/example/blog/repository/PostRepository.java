package com.example.blog.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.blog.dto.PostsWithReactionsCountDto;
import com.example.blog.model.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

@Query(value = """
    SELECT 
        p.id,
        p.title,
        p.description,
        p.media,
        p.created_at AS createdAt,
        COALESCE(SUM(CASE WHEN r.type = 'like' THEN 1 ELSE 0 END), 0) AS likes,
        COALESCE(SUM(CASE WHEN r.type = 'dislike' THEN 1 ELSE 0 END), 0) AS dislikes,
        p.user_id AS creatorId,
        u.username AS creatorUsername
    FROM posts p
    LEFT JOIN reactions r ON r.post_id = p.id
    JOIN users u ON u.id = p.user_id
    GROUP BY p.id, p.title, p.description, p.media, p.created_at, p.user_id, u.username
    """, nativeQuery = true)
List<PostsWithReactionsCountDto> findAllPostsWithReactionsCount();


    // Fetch posts by creator ID with reactions
    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.reactions WHERE p.creator.id = :userId")
    List<Post> findByCreatorIdWithReactions(@Param("userId") Long userId);

    // // Fetch single post by ID with reactions
    // @Query("SELECT p FROM Post p LEFT JOIN FETCH p.reactions WHERE p.id = :id")
    // Optional<Post> findByIdWithReactions(@Param("id") Long id);
    // Your existing methods (keep these)
    Optional<List<Post>> findByCreatorId(Long userId);

    @Override
    Optional<Post> findById(Long id);

    @Override
    void deleteById(Long id);
}

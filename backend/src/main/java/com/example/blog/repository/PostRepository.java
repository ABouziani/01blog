package com.example.blog.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.blog.dto.PostsWithReactionsCountDto;
import com.example.blog.dto.ReactionCount;
import com.example.blog.dtoresponses.HomePostDto;
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
    WHERE p.is_hidden = false
      AND (:lastId IS NULL OR p.id < :lastId)
    GROUP BY p.id, p.title, p.description, p.media, p.created_at, p.user_id, u.username
    ORDER BY p.id DESC
    LIMIT :pageSize
    """, nativeQuery = true)
    List<PostsWithReactionsCountDto> findAllPostsWithReactionsCount(@Param("lastId") Long lastId, @Param("pageSize") int pageSize);

    @Query("""
    SELECT p FROM Post p 
        LEFT JOIN FETCH p.reactions 
        WHERE p.creator.id = :userId
        AND p.isHidden = false
        AND (:lastId IS NULL OR p.id < :lastId)
        ORDER BY p.id DESC
    """)
    List<Post> findByCreatorIdWithReactions(@Param("userId") Long userId,@Param("lastId") Long lastId,Pageable pageable);

    // // Fetch single post by ID with reactions
    // @Query("SELECT p FROM Post p LEFT JOIN FETCH p.reactions WHERE p.id = :id")
    // Optional<Post> findByIdWithReactions(@Param("id") Long id);
    // Your existing methods (keep these)
    Optional<List<Post>> findByCreatorIdAndIsHiddenFalse(Long userId);

    Optional<Post> findByIdAndIsHiddenFalse(Long id);

    @Override
    void deleteById(Long id);

    @Query("""
    SELECT new com.example.blog.dto.ReactionCount(
        COALESCE(SUM(CASE WHEN r.type = 'like' THEN 1 ELSE 0 END), 0),
        COALESCE(SUM(CASE WHEN r.type = 'dislike' THEN 1 ELSE 0 END), 0)
    )
    FROM Reaction r
    WHERE r.post.id = :postId
    """)
    ReactionCount findReactionCountsByPostId(@Param("postId") Long postId);

    @Query(value = """
    SELECT p.id, p.title, u.id AS creatorId, u.username AS creatorUsername, p.media
        FROM posts p
        JOIN users u ON p.user_id = u.id
        WHERE p.user_id IN (
            SELECT uf.followed_id
            FROM user_followers uf
            WHERE uf.follower_id = :userId
        )
        AND p.is_hidden = false
        ORDER BY p.created_at DESC
        LIMIT :n;
    """, nativeQuery = true)
    List<HomePostDto> getRecentPostsForHome(@Param("userId") Long userId, @Param("n") int n);

    @Query(value = """
    SELECT
        p.id AS id, 
        p.title AS title,
        u.id AS creatorId,
        u.username AS creatorUsername,
        p.media AS media
    FROM posts p
    JOIN users u ON p.user_id = u.id
    LEFT JOIN reactions r ON p.id = r.post_id
    WHERE p.is_hidden = false
    GROUP BY p.id, p.title, u.id, u.username, p.media
    ORDER BY COUNT(r.id) DESC
    LIMIT :n
    """, nativeQuery = true)
    List<HomePostDto> getTopPostsForHome(@Param("n") int n);

    @Query("SELECT p FROM Post p WHERE " +
           "LOWER(p.title) LIKE LOWER(CONCAT('%', :term, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :term, '%'))")
    List<Post> searchPosts(@Param("term") String term);

    @Query("""
        SELECT 
            p.id AS id,
            p.title AS title,
            p.description AS description,
            p.media AS media,
            p.createdAt AS createdAt,
            COALESCE(SUM(CASE WHEN r.type = 'LIKE' THEN 1 ELSE 0 END), 0) AS likes,
            COALESCE(SUM(CASE WHEN r.type = 'DISLIKE' THEN 1 ELSE 0 END), 0) AS dislikes,
            p.creator.id AS creatorId,
            p.creator.username AS creatorUsername
        FROM Post p
        LEFT JOIN p.reactions r
        WHERE 
            LOWER(p.title) LIKE LOWER(CONCAT('%', :term, '%')) 
            OR LOWER(p.description) LIKE LOWER(CONCAT('%', :term, '%'))
        GROUP BY 
            p.id, p.title, p.description, p.media, p.createdAt, p.creator.id, p.creator.username
        """)
    List<PostsWithReactionsCountDto> searchPostsProjected(@Param("term") String term);
}

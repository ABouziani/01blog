package com.example.blog.repository;

import java.util.List;
import java.time.LocalDateTime;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.blog.model.Notification;

public interface NotifRepository extends JpaRepository<Notification, Long> {

    // Fetch all notifications from authors that a user follows
    @Query("""
        SELECT n FROM Notification n
        WHERE n.author IN :followingAuthors
        AND (:lastId IS NULL OR n.id < :lastId)
        ORDER BY n.createdAt DESC
        """)
    List<Notification> findByAuthorIn(@Param("followingAuthors") List<com.example.blog.model.User> followingAuthors, @Param("lastId") Long lastId, Pageable pageable);

    // Optional: Fetch notifications newer than a timestamp (for optimization)
    @Query("""
        SELECT n FROM Notification n
        WHERE n.author IN :followingAuthors
          AND n.createdAt > :since
        ORDER BY n.createdAt DESC
        """)
    List<Notification> findByAuthorInAndCreatedAtAfter(
            @Param("followingAuthors") List<com.example.blog.model.User> followingAuthors,
            @Param("since") LocalDateTime since
    );
}

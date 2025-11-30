package com.example.blog.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.blog.dtoresponses.ActiveUsersDto;
import com.example.blog.dtoresponses.UserSearchDto;
import com.example.blog.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByIdAndIsBannedFalse(Long id);

    List<User> findAllByIdNotAndIsBannedFalse(Long id);

    List<User> findAllByIdNot(Long id);

    Optional<User> findByEmailAndIsBannedFalse(String email);

    Optional<User> findByEmail(String email);

    // Optional<User> findByUsernameAndIsBannedFalse(String username);
    Optional<User> findByUsername(String username);

    boolean existsByUsernameAndIsBannedFalse(String username);

    Long countByRoleAndIsBannedFalse(String role);

    boolean existsByEmailAndIsBannedFalse(String email);

    @Query(value = """
    SELECT 
        u.id,
        u.username AS name, 
        COUNT(p.id) FILTER (WHERE p.is_hidden = FALSE) AS posts,
        u.photo AS avatar
        FROM users u
        LEFT JOIN posts p ON u.id = p.user_id
        WHERE u.is_banned = FALSE
        GROUP BY u.id, u.username, u.photo
        ORDER BY posts DESC
        LIMIT :n;
    """, nativeQuery = true)
    List<ActiveUsersDto> getActiveUsersForHome(@Param("n") int n);

    @Query("SELECT u FROM User u WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :term, '%'))")
    List<User> searchUsers(@Param("term") String term);

    @Query("""
    SELECT 
        u.id AS id,
        u.username AS username,
        u.photo AS photo,
        u.bio AS bio,
        u.createdAt AS createdAt,
        SIZE(u.followers) AS followersCount,
        SIZE(u.following) AS followingCount,
        SIZE(u.posts) AS postsCount
    FROM User u
    WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :term, '%'))
    """)
List<UserSearchDto> searchUsersProjected(@Param("term") String term);
}

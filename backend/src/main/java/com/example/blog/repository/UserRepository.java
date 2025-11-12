package com.example.blog.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.blog.dtoresponses.ActiveUsersDto;
import com.example.blog.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    @Override
    Optional<User> findById(Long id);

    List<User> findAllByIdNot(Long id);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    Long countByRole(String role);

    @Query(value = """
    SELECT 
        u.id,
        u.username AS name, 
        COUNT(p.id) AS posts, 
        u.photo AS avatar
        FROM users u
        LEFT JOIN posts p ON u.id = p.user_id
        GROUP BY u.id, u.username, u.photo
        ORDER BY posts DESC
        LIMIT :n;
    """, nativeQuery = true)
    List<ActiveUsersDto> getActiveUsersForHome(@Param("n") int n);
}

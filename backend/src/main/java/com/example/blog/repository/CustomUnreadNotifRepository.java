package com.example.blog.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.blog.model.CustomUnreadNotif;

public interface CustomUnreadNotifRepository extends JpaRepository<CustomUnreadNotif, Long> {

    // Check if a user has marked a notification as unread
    boolean existsByUserIdAndNotificationId(Long userId, Long notificationId);

    // Get all notification IDs that a user has marked as unread
    @Query("SELECT c.notification.id FROM CustomUnreadNotif c WHERE c.user.id = :userId")
    Set<Long> findNotificationIdsMarkedUnreadByUser(@Param("userId") Long userId);

    @Query("""
    SELECT c.notification.id
    FROM CustomUnreadNotif c
    JOIN UserFollow uf
      ON uf.followed = c.notification.author
     AND uf.follower.id = :userId
    WHERE c.user.id = :userId
      AND c.notification.createdAt > uf.followedAt
    """)
    Set<Long> findUnreadNotificationIdsAfterFollowDate(@Param("userId") Long userId);

    // Delete entry when user reads it again
    @Modifying
    @Query("DELETE FROM CustomUnreadNotif c WHERE c.user.id = :userId AND c.notification.id = :notificationId")
    void deleteByUserIdAndNotificationId(@Param("userId") Long userId, @Param("notificationId") Long notificationId);

    // Optional: Delete all custom unread notifs for a user (e.g., on "mark all as read")
    @Modifying
    void deleteByUserId(Long userId);

    // Delete entry when user reads it again
    @Modifying
    @Query("DELETE FROM CustomUnreadNotif c WHERE c.notification.id = :notificationId")
    void deleteByNotificationId(@Param("notificationId") Long notificationId);
}

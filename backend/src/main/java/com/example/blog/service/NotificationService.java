package com.example.blog.service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.blog.dtoresponses.NotificationResponseDto;
import com.example.blog.model.*;
import com.example.blog.repository.*;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotifRepository notificationRepository;
    private final CustomUnreadNotifRepository customUnreadNotifRepository;
    private final UserRepository userRepository; // needed to refresh user's following list if not loaded

    // --- 1. Create notification when a post is published ---
    @Transactional
    public void createNotificationForNewPost(Post post) {
        Notification notification = new Notification();
        notification.setAuthor(post.getCreator());
        notification.setPost(post);
        notification.setCreatedAt(post.getCreatedAt()); // as per your requirement
        notificationRepository.save(notification);
    }

    // --- 2. Get notifications for a viewer (follower) ---
    @Transactional(readOnly = true)
    public List<NotificationResponseDto> getNotificationsForUser(User viewer, Long lastId, int pageSize) {

        User managedViewer = userRepository.findById(viewer.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<User> followingAuthors = new ArrayList<>(managedViewer.getFollowing());

        if (followingAuthors.isEmpty()) {
            return Collections.emptyList();
        }

        List<Notification> notifications;
        Pageable limit = PageRequest.of(0, pageSize);
        if (lastId == 0) {
            notifications = notificationRepository.findByAuthorIn(followingAuthors, null, limit);
        } else {
            notifications = notificationRepository.findByAuthorIn(followingAuthors, lastId, limit);
        }
        Set<Long> customUnreadIds = customUnreadNotifRepository.findNotificationIdsMarkedUnreadByUser(viewer.getId());

        return notifications.stream()
                .map(notif -> {
                    Post post = notif.getPost();
                    User author = notif.getAuthor();

                    boolean isUnread = notif.getCreatedAt().isAfter(viewer.getLastNotifCheck())
                            || customUnreadIds.contains(notif.getId());

                    return new NotificationResponseDto(
                            notif.getId(),
                            post.getId(),
                            post.getTitle(),
                            author.getId(),
                            author.getUsername(),
                            author.getPhoto(),
                            notif.getCreatedAt(), // same as post.createdAt
                            isUnread
                    );
                })
                .sorted((a, b) -> b.getPostCreatedAt().compareTo(a.getPostCreatedAt()))
                .collect(Collectors.toList());
    }

    // --- 3. Mark a notification as unread (after it was read) ---
    @Transactional
    public void markNotificationAsUnread(User user, Long notificationId
    ) {
        // Verify the notification exists and belongs to someone the user follows
        Notification notif = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (!user.getFollowing().contains(notif.getAuthor())) {
            throw new RuntimeException("You cannot mark this notification as unread");
        }

        // Save to custom unread table
        CustomUnreadNotif entry = new CustomUnreadNotif();
        entry.setUser(user);
        entry.setNotification(notif);
        customUnreadNotifRepository.save(entry);
    }

    // --- 4. Mark a notification as read (removes from custom unread) ---
    @Transactional
    public void markNotificationAsRead(User user, Long notificationId
    ) {
        customUnreadNotifRepository.deleteByUserIdAndNotificationId(user.getId(), notificationId);
    }

    // --- 5. Mark all notifications as read ---
    @Transactional
    public void markAllNotificationsAsRead(User user
    ) {
        user.setLastNotifCheck(LocalDateTime.now());
        userRepository.save(user); // or use a dedicated update method

        // Clear all custom unread markers
        customUnreadNotifRepository.deleteByUserId(user.getId());
    }

}

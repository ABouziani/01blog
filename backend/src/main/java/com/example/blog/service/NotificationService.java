package com.example.blog.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.blog.dtoresponses.NotificationResponseDto;
import com.example.blog.model.CustomUnreadNotif;
import com.example.blog.model.Notification;
import com.example.blog.model.Post;
import com.example.blog.model.User;
import com.example.blog.model.UserFollow;
import com.example.blog.repository.CustomUnreadNotifRepository;
import com.example.blog.repository.NotifRepository;
import com.example.blog.repository.UserRepository;


@Service

public class NotificationService {

    @Autowired
    private NotifRepository notificationRepository;

    @Autowired
    private CustomUnreadNotifRepository customUnreadNotifRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void createNotificationForNewPost(Post post) {
        Notification notification = new Notification();
        notification.setAuthor(post.getCreator());
        notification.setPost(post);
        notification.setCreatedAt(post.getCreatedAt());
        notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public List<NotificationResponseDto> getNotificationsForUser(
            User viewer, Long lastId, int pageSize) {

        // Extract authors the user follows
        List<User> followingAuthors = viewer.getFollowing().stream()
                .map(UserFollow::getFollowed)
                .collect(Collectors.toList());

        if (followingAuthors.isEmpty()) {
            return Collections.emptyList();
        }

        Pageable limit = PageRequest.of(0, pageSize);

        List<Notification> notifications
                = (lastId == 0)
                        ? notificationRepository.findNotificationsAfterFollowDate(viewer.getId(), null, limit)
                        : notificationRepository.findNotificationsAfterFollowDate(viewer.getId(), lastId, limit);

        // Fetch all unread custom IDs
        Set<Long> unreadCustomIds
                = customUnreadNotifRepository.findNotificationIdsMarkedUnreadByUser(viewer.getId());

        LocalDateTime lastCheck = viewer.getLastNotifCheck();

        return notifications.stream()
                .map(notif -> {

                    boolean isUnread
                            = notif.getCreatedAt().isAfter(lastCheck) || unreadCustomIds.contains(notif.getId());

                    return new NotificationResponseDto(
                            notif.getId(),
                            notif.getPost().getId(),
                            notif.getPost().getTitle(),
                            notif.getAuthor().getId(),
                            notif.getAuthor().getUsername(),
                            notif.getAuthor().getPhoto(),
                            notif.getCreatedAt(),
                            isUnread
                    );
                })
                .collect(Collectors.toList()); // no sorting needed
    }

    @Transactional
    public ResponseEntity<?> markNotificationAsUnread(User user, Long notificationId) {
        // Verify the notification exists
        Optional<Notification> notifOpt = notificationRepository.findById(notificationId);

        if (notifOpt.isEmpty()) {
            return new ResponseEntity<>("Notification not found", HttpStatus.BAD_REQUEST);
        }

        Notification notif = notifOpt.get();

        // Check if user is following the author
        boolean isFollowingAuthor = user.getFollowing().stream()
                .anyMatch(uf -> uf.getFollowed().equals(notif.getAuthor()));

        if (!isFollowingAuthor) {
            return new ResponseEntity<>("You cannot mark this notification as unread", HttpStatus.BAD_REQUEST);
        }

        // Save to custom unread table
        CustomUnreadNotif entry = new CustomUnreadNotif();
        entry.setUser(user);
        entry.setNotification(notif);
        customUnreadNotifRepository.save(entry);

        return ResponseEntity.ok().build();
    }

    @Transactional
    public ResponseEntity<?> markNotificationAsRead(User user, Long notificationId) {
        Optional<Notification> notifOpt = notificationRepository.findById(notificationId);
        if (notifOpt.isEmpty()) {
            return new ResponseEntity<>("Notification not found", HttpStatus.BAD_REQUEST);
        }
        user.setLastNotifCheck(LocalDateTime.now());
        customUnreadNotifRepository.deleteByUserIdAndNotificationId(user.getId(), notificationId);
        return ResponseEntity.ok().build();
    }

    // --- 5. Mark all notifications as read ---
    @Transactional
    public void markAllNotificationsAsRead(User user) {
        user.setLastNotifCheck(LocalDateTime.now());
        userRepository.save(user);
        customUnreadNotifRepository.deleteByUserId(user.getId());
    }

}

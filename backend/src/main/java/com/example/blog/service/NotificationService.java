package com.example.blog.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
import com.example.blog.repository.CustomUnreadNotifRepository;
import com.example.blog.repository.NotifRepository;
import com.example.blog.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotifRepository notificationRepository;
    private final CustomUnreadNotifRepository customUnreadNotifRepository;
    private final UserRepository userRepository; 


    @Transactional
    public void createNotificationForNewPost(Post post) {
        Notification notification = new Notification();
        notification.setAuthor(post.getCreator());
        notification.setPost(post);
        notification.setCreatedAt(post.getCreatedAt());
        notificationRepository.save(notification);
    }

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
                            notif.getCreatedAt(), 
                            isUnread
                    );
                })
                .sorted((a, b) -> b.getPostCreatedAt().compareTo(a.getPostCreatedAt()))
                .collect(Collectors.toList());
    }

    @Transactional
    public ResponseEntity<?> markNotificationAsUnread(User user, Long notificationId
    ) {
        // Verify the notification exists and belongs to someone the user follows
        Optional<Notification> notifOpt = notificationRepository.findById(notificationId);
        if (notifOpt.isEmpty()) {
            return new ResponseEntity<>("Notification not found", HttpStatus.BAD_REQUEST);
        }
        Notification notif = notifOpt.get();

        if (!user.getFollowing().contains(notif.getAuthor())) {
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

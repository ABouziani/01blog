package com.example.blog.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.blog.dtoresponses.NotificationResponseDto;
import com.example.blog.model.User;
import com.example.blog.service.NotificationService;
import com.example.blog.service.UserDetailsImpl;
import com.example.blog.service.UserService;

@RestController
@RequestMapping("/api/notifications")
public class NotificationsController {

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationService notificationService;

    public NotificationsController() {

    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long loggedUserId = userDetails.getId();
        User user = userService.findById(loggedUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user;
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponseDto>> getNotifications(
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = "10") int pageSize) {
        User viewer = getCurrentUser();
        List<NotificationResponseDto> notifications = notificationService.getNotificationsForUser(viewer, lastId, pageSize);
        return ResponseEntity.ok(notifications);
    }

    @PostMapping("/{notificationId}/mark-unread")
    public ResponseEntity<?> markAsUnread(@PathVariable Long notificationId) {
        User user = getCurrentUser();

        return notificationService.markNotificationAsUnread(user, notificationId);
    }

    @PostMapping("/{notificationId}/mark-read")
    public ResponseEntity<?> markAsRead(@PathVariable Long notificationId) {
        User user = getCurrentUser();
        return notificationService.markNotificationAsRead(user, notificationId);
    }

    // --- 4. Mark all notifications as read ---
    @PostMapping("/mark-all-read")
    public ResponseEntity<?> markAllAsRead() {
        User user = getCurrentUser();
        notificationService.markAllNotificationsAsRead(user);
        return ResponseEntity.ok(Map.of("success", "true"));
    }
}

package com.example.blog.model;

import jakarta.persistence.*;

@Entity
@Table(
    name = "custom_unread_notifications",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "notification_id"})
)
public class CustomUnreadNotif {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // the follower who marked it unread

    @ManyToOne
    @JoinColumn(name = "notification_id", nullable = false)
    private Notification notification;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }
}
package com.example.blog.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author; // the post creator

    @OneToOne
    @JoinColumn(name = "post_id", nullable = false, unique = true)
    private Post post;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    // @OneToMany(mappedBy = "notification", cascade = CascadeType.ALL, orphanRemoval = true)
    // private Set<CustomUnreadNotif> userUnreadStatuses = new HashSet<>();
}

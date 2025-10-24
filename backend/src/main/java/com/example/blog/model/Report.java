package com.example.blog.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Size;

// @Table(name = "reports")
@Entity
@Table(name = "reports", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"reporter_id", "reported_id"})
})
public class Report {

    public Report() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reporter_id", referencedColumnName = "id")
    private User reporter;

    @ManyToOne
    @JoinColumn(name = "reported_id", referencedColumnName = "id")
    private User reported;

    @Size(min = 10, max = 300, message = "Report reason must be between 10 and 300 characters long")
    @Column(nullable = false)
    private String reason;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column
    private String status = "pending";

    // --- Getters & Setters ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getReporter() {
        return reporter;
    }

    public void setReporter(User reporter) {
        this.reporter = reporter;
    }

    public User getReported() {
        return reported;
    }

    public void setReported(User reported) {
        this.reported = reported;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

     public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

package com.example.blog.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.blog.dtoresponses.adminDtos.PostDto;
import com.example.blog.dtoresponses.adminDtos.PostReportDto;
import com.example.blog.dtoresponses.adminDtos.ReportDto;
import com.example.blog.dtoresponses.adminDtos.UserDto;
import com.example.blog.model.Post;
import com.example.blog.model.ReportUsers;
import com.example.blog.model.User;
import com.example.blog.repository.PostAdminRepo;
import com.example.blog.repository.ReportPostsRepository;
import com.example.blog.repository.ReportUserRepository;
import com.example.blog.repository.UserRepository;

@Service
@Transactional
public class AdminService {

    private final UserRepository userRepository;
    private final PostAdminRepo postRepository;
    private final ReportUserRepository reportUserRepository;
    private final ReportPostsRepository reportPostsRepository;

    public AdminService(UserRepository userRepository,
            PostAdminRepo postRepository,
            ReportUserRepository reportUserRepository,ReportPostsRepository reportPostsRepository ) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.reportUserRepository = reportUserRepository;
        this.reportPostsRepository = reportPostsRepository;
    }

    // === USERS ===
    public List<UserDto> getAllUsers(Long loggedUerId) {
        return userRepository.findAllByIdNot(loggedUerId).stream()
                .map(u -> new UserDto(u.getId(), u.getUsername(), u.getEmail(), u.isBanned(), u.getCreatedAt(), u.getRole().equals("ADMIN")))
                .collect(Collectors.toList());
    }

    public void banUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setBanned(true);
        userRepository.save(user);
    }

    public void activateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setBanned(false);
        userRepository.save(user);
    }

    public void makeAdmin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setRole("ADMIN");
        userRepository.save(user);
    }

    public Optional<String> removeAdmin(Long userId) {
        Long count = userRepository.countByRole("ADMIN");
        if (count == 1) {
            return Optional.of("one admin left");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setRole("USER");
        userRepository.save(user);
        return Optional.empty();
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    // === POSTS ===
    public List<PostDto> getAllPosts() {
        return postRepository.findAll().stream()
                .map(p -> new PostDto(
                p.getId(),
                p.getTitle(),
                p.getDescription(),
                p.getCreator().getUsername(),
                p.getCreatedAt(),
                p.isHidden()
        ))
                .collect(Collectors.toList());
    }

    public void hidePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        post.setHidden(true);
        postRepository.save(post);
    }

    public void unhidePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        post.setHidden(false);
        postRepository.save(post);
    }

    public void deletePost(Long postId) {
        postRepository.deleteById(postId);
    }

    // === REPORTS ===
    public List<ReportDto> getAllUsersReports() {
        return reportUserRepository.findAll().stream()
                .map(r -> new ReportDto(
                r.getId(),
                r.getReporter().getId(),
                r.getReporter().getUsername(),
                r.getReported().getId(),
                r.getReported().getUsername(),
                null, // no post reports yet
                r.getReason(),
                r.getCreatedAt(),
                r.getStatus()
        ))
                .collect(Collectors.toList());
    }

    public List<PostReportDto> getAllPostsReports() {
        return reportPostsRepository.findAll().stream()
                .map(r -> new PostReportDto(
                r.getId(),
                r.getReporter().getId(),
                r.getReporter().getUsername(),
                r.getReportedPost(),
                r.getReason(),
                r.getCreatedAt(),
                r.getStatus()
        ))
                .collect(Collectors.toList());
    }

    public void resolveReport(Long reportId, String action) {
        ReportUsers report = reportUserRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        Long reportedUserId = report.getReported().getId();

        switch (action) {
            case "ban_user" -> {
                User user = userRepository.findById(reportedUserId)
                        .orElseThrow(() -> new RuntimeException("Reported user not found"));
                user.setBanned(true);
                userRepository.save(user);
            }
            case "delete" -> {
                reportUserRepository.deleteById(reportId);
                return ;
            }
            case "dismiss" -> {
                // No action needed beyond deleting report
            }
            default ->
                throw new IllegalArgumentException("Invalid action: " + action);
        }

        // ✅ Delete the report after handling (as you requested)
        // reportUserRepository.deleteById(reportId);
        report.setStatus("resolved");
        reportUserRepository.save(report);
    }
}

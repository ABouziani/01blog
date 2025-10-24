package com.example.blog.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import com.example.blog.dtoresponses.adminDtos.PostDto;
import com.example.blog.dtoresponses.adminDtos.ReportDto;
import com.example.blog.dtoresponses.adminDtos.UserDto;
import com.example.blog.model.Post;
import com.example.blog.model.Report;
import com.example.blog.model.User;
import com.example.blog.repository.UserRepository;
import com.example.blog.repository.PostRepository;
import com.example.blog.repository.ReportRepository;

@Service
@Transactional
public class AdminService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final ReportRepository reportRepository;

    public AdminService(UserRepository userRepository,
                        PostRepository postRepository,
                        ReportRepository reportRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.reportRepository = reportRepository;
    }

    // === USERS ===
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
            .map(u -> new UserDto(u.getId(), u.getUsername(), u.getEmail(), u.isBanned()))
            .collect(Collectors.toList());
    }

    public void banUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        user.setBanned(true);
        userRepository.save(user);
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

    public void deletePost(Long postId) {
        postRepository.deleteById(postId);
    }

    // === REPORTS ===
    public List<ReportDto> getPendingReports() {
        return reportRepository.findAll().stream()
            .map(r -> new ReportDto(
                r.getId(),
                r.getReporter().getId(),
                r.getReported().getId(),
                null, // no post reports yet
                r.getReason(),
                r.getCreatedAt()
            ))
            .collect(Collectors.toList());
    }

    public void resolveReport(Long reportId, String action) {
        Report report = reportRepository.findById(reportId)
            .orElseThrow(() -> new RuntimeException("Report not found"));

        Long reportedUserId = report.getReported().getId();

        switch (action) {
            case "ban_user" -> {
                User user = userRepository.findById(reportedUserId)
                    .orElseThrow(() -> new RuntimeException("Reported user not found"));
                user.setBanned(true);
                userRepository.save(user);
            }
            // case "delete_post" -> {
            //     // ❗ You don't have post reports, so this is not applicable.
            //     // Optionally: throw new IllegalArgumentException("Post reports not supported");
            //     // Or ignore.
            // }
            case "dismiss" -> {
                // No action needed beyond deleting report
            }
            default -> throw new IllegalArgumentException("Invalid action: " + action);
        }

        // ✅ Delete the report after handling (as you requested)
        reportRepository.deleteById(reportId);
    }
}
package com.example.blog.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.blog.dtoresponses.adminDtos.PostDto;
import com.example.blog.dtoresponses.adminDtos.PostReportDto;
import com.example.blog.dtoresponses.adminDtos.ReportDto;
import com.example.blog.dtoresponses.adminDtos.UserDto;
import com.example.blog.model.Post;
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
    private final PostService postService;

    public AdminService(UserRepository userRepository,
            PostAdminRepo postRepository,
            ReportUserRepository reportUserRepository, ReportPostsRepository reportPostsRepository, PostService postService) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.reportUserRepository = reportUserRepository;
        this.reportPostsRepository = reportPostsRepository;
        this.postService = postService;
    }

    // === getSummary ===
    public Map getSummary() {
        Long usersCounts = userRepository.count();
        Long postsCounts = postRepository.count();
        Long usersReportsCount = reportUserRepository.count();
        Long postsReportsCount = reportPostsRepository.count();
        return Map.of("usersCounts", usersCounts, "postsCounts", postsCounts, "usersReportsCount", usersReportsCount, "postsReportsCount", postsReportsCount);
    }

    // === USERS ===
    public List<UserDto> getAllUsers(Long loggedUerId) {
        return userRepository.findAllByIdNot(loggedUerId).stream()
                .map(u -> new UserDto(u.getId(), u.getUsername(), u.getEmail(), u.isBanned(), u.getCreatedAt(), u.getRole().equals("ADMIN")))
                .collect(Collectors.toList());
    }

    public ResponseEntity<?> banUser(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return new ResponseEntity<>("User not found", HttpStatus.BAD_REQUEST);
        }
        User user = userOpt.get();
        if (user.getRole().equals("ADMIN")) {
            return new ResponseEntity<>("You cannot ban the admin!", HttpStatus.BAD_REQUEST);
        }

        user.setBanned(true);
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<?> activateUser(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return new ResponseEntity<>("User not found", HttpStatus.BAD_REQUEST);
        }

        User user = userOpt.get();

        user.setBanned(false);
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }

    public void makeAdmin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setRole("ADMIN");
        userRepository.save(user);
    }

    public Optional<String> removeAdmin(Long userId) {
        Long count = userRepository.countByRoleAndIsBannedFalse("ADMIN");
        if (count == 1) {
            return Optional.of("one admin left");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setRole("USER");
        userRepository.save(user);
        return Optional.empty();
    }

    public ResponseEntity<?> deleteUser(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return new ResponseEntity<>("User not found", HttpStatus.BAD_REQUEST);
        }
        userRepository.deleteById(userId);
        return ResponseEntity.ok().build();
    }

    // === POSTS ===
    public List<PostDto> getAllPosts() {
        return postRepository.findAll().stream()
                .map(p -> new PostDto(
                p.getId(),
                p.getTitle(),
                p.getDescription(),
                p.getCreator().getId(),
                p.getCreator().getUsername(),
                p.getCreatedAt(),
                p.isHidden()
        ))
                .collect(Collectors.toList());
    }

    public ResponseEntity<?> hidePost(Long postId) {
        Optional<Post> postOpt = postRepository.findById(postId);
        if (postOpt.isEmpty()) {
            return new ResponseEntity<>("Post not found", HttpStatus.BAD_REQUEST);
        }

        Post post = postOpt.get();
        post.setHidden(true);
        postRepository.save(post);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<?> unhidePost(Long postId) {
        Optional<Post> postOpt = postRepository.findById(postId);
        if (postOpt.isEmpty()) {
            return new ResponseEntity<>("Post not found", HttpStatus.BAD_REQUEST);
        }

        Post post = postOpt.get();
        post.setHidden(false);
        postRepository.save(post);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<?> deletePost(Long postId) {
        Optional<Post> postOpt = postRepository.findById(postId);
        if (postOpt.isEmpty()) {
            return new ResponseEntity<>("Post not found", HttpStatus.BAD_REQUEST);
        }
        postRepository.deleteById(postId);
        return ResponseEntity.ok().build();
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

    public ResponseEntity<?> resolveReport(Long reportedUserId, String action) {

        Optional<User> reportedUserOpt = userRepository.findById(reportedUserId);

        if (reportedUserOpt.isEmpty()) {
            return new ResponseEntity<>("User not found!", HttpStatus.BAD_REQUEST);
        }

        User reportedUser = reportedUserOpt.get();

        switch (action) {
            case "ban_user" -> {
                if (reportedUser.getRole().equals("ADMIN")) {
                    return new ResponseEntity<>("You cannot ban the admin!", HttpStatus.BAD_REQUEST);
                }
                reportedUser.setBanned(true);
                userRepository.save(reportedUser);
                reportUserRepository.deleteByReported(reportedUser);
                return ResponseEntity.ok().build();
            }
            case "delete" -> {
                reportUserRepository.deleteByReported(reportedUser);
                return ResponseEntity.ok().build();
            }
            case "dismiss" -> {
                reportUserRepository.deleteByReported(reportedUser);
                return ResponseEntity.ok().build();
            }
            default ->
                throw new IllegalArgumentException("Invalid action: " + action);
        }

    }

    public ResponseEntity<?> resolvePostReport(Long postId, String action) {
        Optional<Post> postOpt = postRepository.findById(postId);
        if (postOpt.isEmpty()) {
            return new ResponseEntity<>("Post not found!", HttpStatus.BAD_REQUEST);
        }

        Post reportedPost = postOpt.get();

        switch (action) {
            case "hide_post" -> {
                reportedPost.setHidden(true);
                postRepository.save(reportedPost);
                reportPostsRepository.deleteByReportedpost(reportedPost);
                return ResponseEntity.ok().build();
            }
            case "delete_post" -> {
                postRepository.delete(reportedPost);
                reportPostsRepository.deleteByReportedpost(reportedPost);
                // postService.deletePost(reportedPost.getId(), loggedUserId);
                return ResponseEntity.ok().build();
            }
            case "delete_report" -> {
                reportPostsRepository.deleteByReportedpost(reportedPost);
                return ResponseEntity.ok().build();
            }
            case "dismiss" -> {
                reportPostsRepository.deleteByReportedpost(reportedPost);
                return ResponseEntity.ok().build();
            }
            default ->
                throw new IllegalArgumentException("Invalid action: " + action);
        }

        // report.setStatus("resolved");
        // reportPostsRepository.save(report);
        // return ResponseEntity.ok().build();
    }
}

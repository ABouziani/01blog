package com.example.blog.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.blog.dtoresponses.adminDtos.PostDto;
import com.example.blog.dtoresponses.adminDtos.PostReportDto;
import com.example.blog.dtoresponses.adminDtos.ReportDto;
import com.example.blog.dtoresponses.adminDtos.ResolvePostReportRequest;
import com.example.blog.dtoresponses.adminDtos.ResolveReportRequest;
import com.example.blog.dtoresponses.adminDtos.UserDto;
import com.example.blog.service.AdminService;
import com.example.blog.service.UserDetailsImpl;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // === Summary ===
    @GetMapping("/summary")
    public ResponseEntity<?> getSummary(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long loggedUserId = userDetails.getId();
        return ResponseEntity.ok(adminService.getSummary());
    }

    // === USERS ===
    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long loggedUserId = userDetails.getId();
        return ResponseEntity.ok(adminService.getAllUsers(loggedUserId));
    }

    @PutMapping("/users/{id}/ban")
    public ResponseEntity<?> banUser(@PathVariable Long id) {
        return adminService.banUser(id);
    }

    @PutMapping("/users/{id}/activate")
    public ResponseEntity<?> activateUser(@PathVariable Long id) {;
        return adminService.activateUser(id);
    }

    @PutMapping("/users/{id}/make-admin")
    public ResponseEntity<Void> makeAdminUser(@PathVariable Long id) {
        adminService.makeAdmin(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/users/{id}/remove-admin")
    public ResponseEntity<?> removeAdmin(@PathVariable Long id) {
        Optional<String> opt = adminService.removeAdmin(id);
        if (opt.isPresent()) {
            return new ResponseEntity<>(opt.get(), HttpStatus.FORBIDDEN);
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        return adminService.deleteUser(id);
    }

    // === POSTS ===
    @GetMapping("/posts")
    public ResponseEntity<List<PostDto>> getAllPosts() {
        return ResponseEntity.ok(adminService.getAllPosts());
    }

    @PostMapping("/posts/{id}/hide")
    public ResponseEntity<?> hidePost(@PathVariable Long id) {
        return adminService.hidePost(id);
    }

    @PutMapping("/posts/{id}/unhide")
    public ResponseEntity<?> unhidePost(@PathVariable Long id) {
        return adminService.unhidePost(id);
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id) {
        return adminService.deletePost(id);
    }

    // === REPORTS ===
    @GetMapping("/reports")
    public ResponseEntity<?> getAllReports() {
        List<PostReportDto> postReports = adminService.getAllPostsReports();
        List<ReportDto> userReports = adminService.getAllUsersReports();
        return ResponseEntity.ok(Map.of(
                "postReports", postReports,
                "userReports", userReports
        ));
    }

    @PostMapping("/userreports/{userid}/{reportid}/resolve")
    public ResponseEntity<?> resolveReport(
            @PathVariable Long userid,
            @PathVariable Long reportid,
            @RequestBody ResolveReportRequest request
    ) {
        return adminService.resolveReport(userid, request.action(), reportid);
    }

    @PostMapping("/postreports/{postId}/{reportid}/resolve")
    public ResponseEntity<?> resolvePostReport(
            @PathVariable Long postId,
            @PathVariable Long reportid,
            @RequestBody ResolvePostReportRequest request
    ) {
        return adminService.resolvePostReport(postId, request.action(), reportid);
    }
}

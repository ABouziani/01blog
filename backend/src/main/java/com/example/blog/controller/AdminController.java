package com.example.blog.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;

import java.util.List;

import com.example.blog.dtoresponses.adminDtos.*;
import com.example.blog.service.AdminService;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // === USERS ===
    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @PostMapping("/users/{id}/ban")
    public ResponseEntity<Void> banUser(@PathVariable Long id) {
        adminService.banUser(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    // === POSTS ===
    @GetMapping("/posts")
    public ResponseEntity<List<PostDto>> getAllPosts() {
        return ResponseEntity.ok(adminService.getAllPosts());
    }

    @PostMapping("/posts/{id}/hide")
    public ResponseEntity<Void> hidePost(@PathVariable Long id) {
        adminService.hidePost(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        adminService.deletePost(id);
        return ResponseEntity.ok().build();
    }

    // === REPORTS ===
    @GetMapping("/reports")
    public ResponseEntity<List<ReportDto>> getPendingReports() {
        return ResponseEntity.ok(adminService.getPendingReports());
    }

    @PostMapping("/reports/{id}/resolve")
    public ResponseEntity<Void> resolveReport(
        @PathVariable Long id,
        @RequestBody ResolveReportRequest request
    ) {
        adminService.resolveReport(id, request.action());
        return ResponseEntity.ok().build();
    }
}
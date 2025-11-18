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

    // === USERS ===
    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long loggedUserId = userDetails.getId();
        return ResponseEntity.ok(adminService.getAllUsers(loggedUserId));
    }

    @PutMapping("/users/{id}/ban")
    public ResponseEntity<Void> banUser(@PathVariable Long id) {
        adminService.banUser(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/users/{id}/activate")
    public ResponseEntity<Void> activateUser(@PathVariable Long id) {
        adminService.activateUser(id);
        return ResponseEntity.ok().build();
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

    @PutMapping("/posts/{id}/unhide")
    public ResponseEntity<Void> unhidePost(@PathVariable Long id) {
        adminService.unhidePost(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        adminService.deletePost(id);
        return ResponseEntity.ok().build();
    }

    // === REPORTS ===
    @GetMapping("/reports")
    public ResponseEntity<?> getAllReports() {
        List<PostReportDto> postReports = adminService.getAllPostsReports();
        List<ReportDto> userReports = adminService.getAllUsersReports();
        return ResponseEntity.ok(Map.of(
            "postReports",postReports,
            "userReports",userReports
        ));
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

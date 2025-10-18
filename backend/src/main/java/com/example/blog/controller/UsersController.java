package com.example.blog.controller;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.blog.dtoresponses.EntityDtoMapper;
import com.example.blog.dtoresponses.UserDto;
import com.example.blog.model.User;
import com.example.blog.repository.ReportRepository;
import com.example.blog.service.UserDetailsImpl;
import com.example.blog.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UsersController {

    @Autowired
    private UserService userService;

    @Autowired
    private ReportRepository reportRepository;

    // POST /api/users/register
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@Valid @RequestBody User user) {
        // System.out.println("Received password: " + user.getPassword());
        User savedUser = userService.registerUser(user);
        return ResponseEntity.ok(savedUser); // returns 200 + JSON of saved user
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id, Authentication authentication) {
        User user = userService.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        // Ensure posts are fetched (avoid LazyInitializationException)

        // Get current user ID
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long loggedUserId = userDetails.getId();

        if (reportRepository.existsByReporterIdAndReportedId(loggedUserId, user.getId())) {
            user.setIsAlreadyReported(true);
        }

        Hibernate.initialize(user.getPosts());



        return ResponseEntity.ok(EntityDtoMapper.toUserDto(user));
    }
    // // POST /api/users/login
    // @PostMapping("/login")
    // public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
    //     String identifier = body.get("identifier"); // username or email
    //     String password = body.get("password");

    //     Optional<User> userOpt = userService.findByUsernameOrEmail(identifier);
    //     if (userOpt.isPresent() && userService.checkPassword(userOpt.get(), password)) {
    //         return ResponseEntity.ok("Login successful!");
    //     }
    //     return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials\n");
    // }
}

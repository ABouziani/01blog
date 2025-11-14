package com.example.blog.controller;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.blog.dtoresponses.EntityDtoMapper;
import com.example.blog.dtoresponses.UserDto;
import com.example.blog.model.User;
import com.example.blog.repository.ReportRepository;
import com.example.blog.repository.UserRepository;
import com.example.blog.service.UserDetailsImpl;
import com.example.blog.service.UserService;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@RestController
@RequestMapping("/api/users")
public class UsersController {

    @Autowired
    private UserService userService;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private UserRepository userRepository;

    // POST /api/users/register
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
            @RequestParam("username") @NotBlank @Size(min = 3) String username,
            @RequestParam("email") @NotBlank @Size(min = 6) String email,
            @RequestParam("password") @NotBlank @Size(min = 6) String password,
            @RequestParam("passverif") @NotBlank @Size(min = 6) String passverif,
            @RequestParam("bio") @Size(max = 500) String bio,
            @RequestParam(value = "photo", required = false) MultipartFile photo,
            Authentication authentication
    ) {
        if (!password.equals(passverif)) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Passwords don't match");
        }

        if (userService.existsByUsername(username)) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Username already taken");
        }

        if (userService.existsByEmail(email)) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Email already registered");
        }
        User savedUser = userService.registerUser(username, email, password, bio, photo);
        return ResponseEntity.ok(savedUser);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id, Authentication authentication) {
        if (id == 0) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            id = userDetails.getId();
        }
        User user = userService.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        // Ensure posts are fetched (avoid LazyInitializationException)

        // Get current user ID
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long loggedUserId = userDetails.getId();

        User loggedUser = userService.findById(loggedUserId)
                .orElseThrow(() -> new RuntimeException("Bad request!"));
        // Ensure posts are fetched (avoid LazyInitializationException)

        if (reportRepository.existsByReporterIdAndReportedId(loggedUserId, user.getId())) {
            user.setIsAlreadyReported(true);
        }

        if (loggedUser.getFollowing().contains(user)) {
            user.setIsAlreadyFollowed(true);
        }

        if (Objects.equals(loggedUser.getId(), user.getId())) {
            user.setOwner(true);
        }

        // Hibernate.initialize(user.getPosts());

        //Mark notifs from this user as read
        // loggedUser.markAuthorNotificationsAsRead(user.getId());
        // userRepository.save(loggedUser);
        return ResponseEntity.ok(EntityDtoMapper.toUserDto(user));
    }

    @PutMapping(value = "/edit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserDto> updateUser(
            @RequestParam("bio") @Size(max = 500) String bio,
            @RequestParam("email") @NotBlank @Size(min = 10, max = 1000) String email,
            @RequestParam("noPhoto") @NotBlank String noPhoto,
            @RequestParam(value = "media", required = false) MultipartFile media,
            Authentication authentication
    ) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long loggedUserId = userDetails.getId();

        User updatedUser = userService.updatePost(loggedUserId, bio, email, media, noPhoto == "true");

        return ResponseEntity.ok(EntityDtoMapper.toUserDto(updatedUser));
    }

    // // POST /api/users/register
    // @PostMapping("/follow")
    // public ResponseEntity<User> FollowUser(@Valid @RequestBody User user) {
    //     // System.out.println("Received password: " + user.getPassword());
    //     User savedUser = userService.registerUser(user);
    //     return ResponseEntity.ok(savedUser); // returns 200 + JSON of saved user
    // }
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
    // @GetMapping("/notifs")
    // public ResponseEntity<?> getNotifs(Authentication authentication) {
    //     // Get current user ID
    //     UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
    //     Long loggedUserId = userDetails.getId();
    //     User loggedUser = userService.findById(loggedUserId)
    //             .orElseThrow(() -> new RuntimeException("Bad request!"));
    //     return ResponseEntity.ok(loggedUser.getUnReadNotifs());
    // }
}

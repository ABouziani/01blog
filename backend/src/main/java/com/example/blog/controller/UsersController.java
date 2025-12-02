package com.example.blog.controller;

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

import com.example.blog.model.User;
import com.example.blog.repository.ReportUserRepository;
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
    private ReportUserRepository reportUserRepository;

    @Autowired
    private UserRepository userRepository;

    // POST /api/users/register
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
            @RequestParam("username") @NotBlank @Size(min = 3, max = 10) String username,
            @RequestParam("email") @NotBlank @Size(min = 6, max = 20) String email,
            @RequestParam("password") @NotBlank @Size(min = 6, max = 10) String password,
            @RequestParam("passverif") @NotBlank @Size(min = 6, max = 10) String passverif,
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
    public ResponseEntity<?> getUser(@PathVariable Long id, Authentication authentication) {
        if (authentication == null) {
            return new ResponseEntity<>("UNAUTHORIZED", HttpStatus.UNAUTHORIZED);

        }
        if (id == 0) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            id = userDetails.getId();
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long loggedUserId = userDetails.getId();

        ResponseEntity<?> resp = userService.getUser(id, loggedUserId);

        return resp;
    }

    @PutMapping(value = "/edit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateUser(
            @RequestParam("bio") @Size(max = 500) String bio,
            @RequestParam("email") @NotBlank @Size(min = 10, max = 1000) String email,
            @RequestParam("noPhoto") @NotBlank String noPhoto,
            @RequestParam(value = "media", required = false) MultipartFile media,
            Authentication authentication
    ) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long loggedUserId = userDetails.getId();
        ResponseEntity<?> updatedUser = userService.updateUser(loggedUserId, bio, email, media, noPhoto.equals("true"));

        return updatedUser;
    }
}

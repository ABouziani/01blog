package com.example.blog.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.blog.dtoresponses.ActiveUsersDto;
import com.example.blog.exceptions.ResourceNotFoundException;
import com.example.blog.model.User;
import com.example.blog.repository.UserRepository;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(PostService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileStorageService fileStorageService;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User registerUser(User user) {
        user.setCreatedAt(LocalDateTime.now());
        // Hash the password before saving
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        return userRepository.save(user);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByUsernameOrEmail(String identifier) {
        Optional<User> user = findByUsername(identifier);
        if (user.isEmpty()) {
            user = findByEmail(identifier);
        }
        return user;
    }

    // Optional: validate password
    public boolean checkPassword(User user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }

    // Login
    public Optional<User> loginUser(String email, String rawPassword) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(rawPassword, user.getPassword())) {
                return Optional.of(user); // Login success
            }
        }
        return Optional.empty(); // Login failed
    }

    public List<ActiveUsersDto> getActiveUsersForHome(int number) {
        return userRepository.getActiveUsersForHome(number);
    }

    public User updatePost(Long loggedUserId, String bio, String email, MultipartFile media, boolean noPhoto) {
        String mediaUrl = null;
        if (media != null && !media.isEmpty()) {
            mediaUrl = fileStorageService.storeFile(media); // e.g., returns "/uploads/abc.jpg"
        }

        User loggedUser = userRepository.findById(loggedUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Bad request"));

        //delete old profile photo
        if ((media != null && !media.isEmpty()) || noPhoto) {
            try {
                String uploadDir = System.getProperty("user.dir") + "/uploads/";
                Path filePath;
                filePath = Paths.get(uploadDir, loggedUser.getPhoto().replace("/api/files/", ""));
                boolean deleted = Files.deleteIfExists(filePath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 3. Update fields
        loggedUser.setEmail(email);
        loggedUser.setBio(bio);
        loggedUser.setPhoto(mediaUrl);

        try {
            return userRepository.save(loggedUser);
        } catch (Exception e) {
            logger.error("Error saving profile: ", e);

            // 👇 Unwrap and log root cause
            Throwable cause = e;
            while (cause.getCause() != null) {
                cause = cause.getCause();
            }
            logger.error("Root cause: {}", cause.getMessage());

            throw e;
        }
    }
}

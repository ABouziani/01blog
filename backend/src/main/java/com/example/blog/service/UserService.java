package com.example.blog.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.blog.dtoresponses.ActiveUsersDto;
import com.example.blog.dtoresponses.EntityDtoMapper;
import com.example.blog.model.User;
import com.example.blog.repository.ReportUserRepository;
import com.example.blog.repository.UserRepository;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(PostService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private ReportUserRepository reportUserRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User registerUser(String username, String email, String password, String bio, MultipartFile photo) {
        String mediaUrl = null;
        if (photo != null && !photo.isEmpty()) {
            mediaUrl = fileStorageService.storeFile(photo);
        }
        User user = new User();
        user.setEmail(email);
        user.setUsername(username);
        user.setBio(bio);
        user.setCreatedAt(LocalDateTime.now());
        user.setLastNotifCheck(LocalDateTime.now());
        String hashedPassword = passwordEncoder.encode(password);
        user.setPassword(hashedPassword);
        user.setPhoto(mediaUrl);
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

    public ResponseEntity<?> updatePost(Long loggedUserId, String bio, String email, MultipartFile media, boolean noPhoto) {
        System.out.println("------------------------------");
        System.out.println(noPhoto);
        Optional<User> userOpt = userRepository.findById(loggedUserId);
        if (userOpt.isEmpty()) {
            return new ResponseEntity<>("Bad request", HttpStatus.BAD_REQUEST);
        }
        User loggedUser = userOpt.get();

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

        String mediaUrl = null;
        boolean addMedia = false;
        if (media != null && !media.isEmpty()) {
            addMedia = true;
            mediaUrl = fileStorageService.storeFile(media); // e.g., returns "/uploads/abc.jpg"
        }

        // 3. Update fields
        loggedUser.setEmail(email);
        loggedUser.setBio(bio);
        if (mediaUrl != null) {
            loggedUser.setPhoto(mediaUrl);
        } else if (noPhoto) {
            loggedUser.setPhoto(null);
        }

        try {
            userRepository.save(loggedUser);
            return ResponseEntity.ok(EntityDtoMapper.toUserDto(loggedUser));
        } catch (Exception e) {
            return new ResponseEntity<>("Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> getUser(Long id, Long loggedUserId) {
        Optional<User> userOpt = findById(id);
        if (userOpt.isEmpty()) {
            return new ResponseEntity<>("User not found", HttpStatus.BAD_REQUEST);
        }
        User user = userOpt.get();

        Optional<User> loggedUserOpt = findById(loggedUserId);
        if (loggedUserOpt.isEmpty()) {
            return new ResponseEntity<>("Bad request!", HttpStatus.BAD_REQUEST);
        }
        User loggedUser = loggedUserOpt.get();

        if (reportUserRepository.existsByReporterIdAndReportedId(loggedUserId, user.getId())) {
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
}

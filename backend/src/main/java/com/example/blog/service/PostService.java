package com.example.blog.service;

import java.io.IOException;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.example.blog.dto.PostsWithReactionsCountDto;
import com.example.blog.dtoresponses.HomePostDto;
import com.example.blog.exceptions.ResourceNotFoundException;
import com.example.blog.model.Notification;
import com.example.blog.model.Post;
import com.example.blog.model.User;
import com.example.blog.repository.CustomUnreadNotifRepository;
import com.example.blog.repository.PostRepository;
import com.example.blog.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class PostService {

    private static final Logger logger = LoggerFactory.getLogger(PostService.class);

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomUnreadNotifRepository customNotificationRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private UserService userService;

    public List<Post> getPostsByUserId(Long userId, Long lastId, int pageSize) {
        Pageable limit = PageRequest.of(0, pageSize);
        if (lastId == 0) {
            return postRepository.findByCreatorIdWithReactions(userId, null, limit);
        } else {
            return postRepository.findByCreatorIdWithReactions(userId, lastId, limit);
        }
    }

    public List<PostsWithReactionsCountDto> getAllPosts(Long lastId, int pageSize) {

        if (lastId == 0) {
            return postRepository.findAllPostsWithReactionsCount(null, pageSize);
        } else {
            return postRepository.findAllPostsWithReactionsCount(lastId, pageSize);
        }
    }

    public ResponseEntity<?> getPost(Long postid, Long loggedUserId) {

        Optional<Post> postOpt = postRepository.findByIdAndIsHiddenFalse(postid);
        if (postOpt.isEmpty()) {
            return new ResponseEntity<>("Post not found", HttpStatus.NOT_FOUND);
        }
        Post post = postOpt.get();

        Optional<User> loggedUserOpt = userRepository.findById(loggedUserId);
        if (loggedUserOpt.isEmpty()) {
            return new ResponseEntity<>("Bad request", HttpStatus.BAD_REQUEST);
        }
        User loggedUser = loggedUserOpt.get();

        if (Objects.equals(post.getCreator().getId(), loggedUserId) || loggedUser.getRole().equals("ADMIN")) {
            post.setIsOwner(true);
        }

        return ResponseEntity.ok(post);

    }

    public Optional<Post> getPostById(Long postId) {
        return postRepository.findByIdAndIsHiddenFalse(postId);
    }

    public Post createPost(String title, String description, MultipartFile media, User creator) {
        String mediaUrl = null;
        if (media != null && !media.isEmpty()) {
            mediaUrl = fileStorageService.storeFile(media); // e.g., returns "/uploads/abc.jpg"
        }

        Post post = new Post();
        post.setTitle(title);
        post.setDescription(description);
        post.setMedia(mediaUrl);
        post.setCreator(creator);
        post.setCreatedAt(LocalDateTime.now());
        Post savedPost = postRepository.save(post);

        return savedPost;
    }

    public Post updatePost(Long postId, String title, String description, MultipartFile media, Long requesterId) {
        String mediaUrl = null;
        if (media != null && !media.isEmpty()) {
            mediaUrl = fileStorageService.storeFile(media); // e.g., returns "/uploads/abc.jpg"
        }
        // 1. Find post
        Post post = postRepository.findByIdAndIsHiddenFalse(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        User loggedUser = userRepository.findById(requesterId)
                .orElseThrow(() -> new ResourceNotFoundException("Bad request"));
        String requesterRole = loggedUser.getRole();

        if (!post.getCreator().getId().equals(requesterId) && !requesterRole.equals("ADMIN")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only modify your own posts");
        }

        //delete old post
        try {
            String uploadDir = System.getProperty("user.dir") + "/uploads/";
            Path filePath;
            filePath = Paths.get(uploadDir, post.getMedia().replace("/api/files/", ""));
            boolean deleted = Files.deleteIfExists(filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 3. Update fields
        post.setTitle(title);
        post.setDescription(description);
        post.setMedia(mediaUrl);

        try {
            return postRepository.save(post);
        } catch (Exception e) {
            logger.error("Error saving post: ", e);

            // 👇 Unwrap and log root cause
            Throwable cause = e;
            while (cause.getCause() != null) {
                cause = cause.getCause();
            }
            logger.error("Root cause: {}", cause.getMessage());

            throw e;
        }
    }

    @Transactional
    public void deletePost(Long postId, Long requesterId) {
        Post post = postRepository.findByIdAndIsHiddenFalse(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        User user = userRepository.findById(requesterId)
                .orElseThrow(() -> new ResourceNotFoundException("Bad request"));

        if (!user.getRole().equals("ADMIN")) {
            if (!post.getCreator().getId().equals(requesterId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only delete your own posts");
            }
        }

        if (post.getMedia() != null) {

            String uploadDir = System.getProperty("user.dir") + "/uploads/";
            Path filePath;
            filePath = Paths.get(uploadDir, post.getMedia().replace("/api/files/", ""));
            try {
                boolean deleted = Files.deleteIfExists(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Notification n = post.getNotification();
        customNotificationRepository.deleteByNotificationId(n.getId());

        postRepository.delete(post);
    }

    public List<HomePostDto> getRecentPostsForHome(Long userId, int number) {
        return postRepository.getRecentPostsForHome(userId, number);
    }

    public List<HomePostDto> getTopPostsForHome(int number) {
        return postRepository.getTopPostsForHome(number);
    }

}

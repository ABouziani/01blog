package com.example.blog.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.example.blog.dto.PostsWithReactionsCountDto;
import com.example.blog.exceptions.ResourceNotFoundException;
import com.example.blog.model.Notification;
import com.example.blog.model.Post;
import com.example.blog.model.User;
import com.example.blog.repository.CustomUnreadNotifRepository;
import com.example.blog.repository.NotifRepository;
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

    public List<Post> getPostsByUserId(Long userId) {
        return postRepository.findByCreatorIdWithReactions(userId);
    }

    public List<PostsWithReactionsCountDto> getAllPosts() {
        return postRepository.findAllPostsWithReactionsCount();
    }

    public Optional<Post> getPostById(Long postId) {
        return postRepository.findById(postId);
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
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        // 2. Check ownership

        if (!post.getCreator().getId().equals(requesterId)) {
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
        Post post = postRepository.findById(postId)
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
}

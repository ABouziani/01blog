package com.example.blog.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.blog.dto.NewPostRequest;
import com.example.blog.model.Post;
import com.example.blog.model.User;
import com.example.blog.repository.PostRepository;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

     @Autowired
    private UserService userService;

    public Optional<List<Post>> getPostsByUserId(Long userId) {
        return postRepository.findByCreatorId(userId);
    }

    public Post createPost(NewPostRequest newPostRequest, Long creatorId) {
        // 1. Map newPostRequest to Entity
        Post post = new Post();
        post.setTitle(newPostRequest.getTitle());
        post.setDescription(newPostRequest.getDescription());
        post.setMedia(newPostRequest.getMedia());

        // 2. Set creator — fetched by ID (from security context usually)
        User creator = userService.findById(creatorId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        post.setCreator(creator);

        // 3. Save and return
        return postRepository.save(post);
    }
}
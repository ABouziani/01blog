package com.example.blog.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.blog.dto.PostsWithReactionsCountDto;
import com.example.blog.dtoresponses.UserSearchDto;
import com.example.blog.repository.PostRepository;
import com.example.blog.repository.UserRepository;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @GetMapping("/users")
    public ResponseEntity<List<UserSearchDto>> searchUsers(@RequestParam String query) {
        List<UserSearchDto> users = userRepository.searchUsersProjected(query.trim());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/posts")
    public ResponseEntity<List<PostsWithReactionsCountDto>> searchPosts(@RequestParam String query) {
        List<PostsWithReactionsCountDto> posts = postRepository.searchPostsProjected(query.trim());
        return ResponseEntity.ok(posts);
    }
}

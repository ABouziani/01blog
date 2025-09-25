package com.example.blog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.blog.dto.NewCommentRequest;
import com.example.blog.model.Comment;
import com.example.blog.service.CommentService;
import com.example.blog.service.UserDetailsImpl;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/comments")
public class CommentsController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/add")
    public ResponseEntity<Comment> addComment(@Valid @RequestBody NewCommentRequest commentReq) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        Long loggedUserId = userDetails.getId();
        Comment savedComment = commentService.addComment(commentReq,loggedUserId);
        return ResponseEntity.ok(savedComment); 
    }

    
}

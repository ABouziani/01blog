package com.example.blog.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.blog.dto.NewReactionRequest;
import com.example.blog.model.Reaction;
import com.example.blog.service.ReactionService;
import com.example.blog.service.UserDetailsImpl;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/reactions")
public class ReactionsController {

    @Autowired
    private ReactionService reactionService;

    @PostMapping("/add")
    public ResponseEntity<Reaction> addReaction(@Valid @RequestBody NewReactionRequest reaction) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        Long loggedUserId = userDetails.getId();
        Reaction savedReaction = reactionService.addorupdateReaction(reaction,loggedUserId);
        return ResponseEntity.ok(savedReaction); // returns 200 + JSON of saved user
    }

     @GetMapping("/getpostreactions/{postid}")
    public List<Reaction> getPosts(@PathVariable Long postid) {
        return reactionService.getReactionsByPostId(postid).orElse(List.of());
    }

}

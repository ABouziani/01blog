package com.example.blog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.blog.model.User;
import com.example.blog.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // POST /api/users/register
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        System.out.println("Received password: " + user.getPassword());
        User savedUser = userService.registerUser(user);
        return ResponseEntity.ok(savedUser); // returns 200 + JSON of saved user
    }

}

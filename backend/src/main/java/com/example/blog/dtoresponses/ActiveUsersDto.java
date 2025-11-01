package com.example.blog.dtoresponses;

public record ActiveUsersDto (
        String name, 
        long posts, 
        String avatar 
     ) {}
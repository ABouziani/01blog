package com.example.blog.dtoresponses;

public record ActiveUsersDto (
        long id,
        String name, 
        long posts, 
        String avatar 
     ) {}
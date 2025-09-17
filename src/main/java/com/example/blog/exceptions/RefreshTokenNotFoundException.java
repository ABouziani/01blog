package com.example.blog.exceptions;


public class RefreshTokenNotFoundException extends TokenRefreshException {
    public RefreshTokenNotFoundException(String token) {
        super("Refresh token not found: " + token);
    }
}
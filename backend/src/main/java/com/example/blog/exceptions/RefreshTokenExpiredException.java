package com.example.blog.exceptions;


public class RefreshTokenExpiredException extends TokenRefreshException {
    public RefreshTokenExpiredException(String token) {
        super("Refresh token expired: " + token);
    }
}
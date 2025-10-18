package com.example.blog.exceptions;


public class TokenManagementException extends TokenRefreshException {
    public TokenManagementException(String token) {
        super("Refresh token expired: " + token);
    }
}
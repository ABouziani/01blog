package com.example.blog.dto;


import java.time.Instant;

public class ErrorResponse {
    private int statusCode;
    private String message;
    private Instant timestamp;

    // Constructor
    public ErrorResponse(int statusCode, String message, Instant timestamp) {
        this.statusCode = statusCode;
        this.message = message;
        this.timestamp = timestamp;
    }

    // Getters (required for JSON serialization)
    public int getStatusCode() { return statusCode; }
    public String getMessage() { return message; }
    public Instant getTimestamp() { return timestamp; }

    // Optional: No-args constructor (if using Jackson and want to be safe)
    public ErrorResponse() {}
}
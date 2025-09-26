package com.example.blog.exceptions;

public class DuplicateReportException extends RuntimeException {
    public DuplicateReportException() {
        super("You already reported this user!");
    }
}
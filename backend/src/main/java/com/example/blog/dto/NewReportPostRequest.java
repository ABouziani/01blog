package com.example.blog.dto;

public class NewReportPostRequest {

    public NewReportPostRequest() {
    }

    private Long reportedPostId;

    private String reason;

    public Long getReportedPostId() {
        return reportedPostId;
    }

    public String getReason() {
        return reason;
    }

}

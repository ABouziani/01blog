package com.example.blog.dto;

public class NewReportRequest {

    public NewReportRequest() {
    }

    private Long reportedId;

    private String reason;

    public Long getReportedId() {
        return reportedId;
    }

    public String getReason() {
        return reason;
    }

}

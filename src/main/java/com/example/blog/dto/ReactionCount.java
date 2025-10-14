package com.example.blog.dto;

public class ReactionCount {
    private long likeCount;
    private long dislikeCount;

    public ReactionCount(long likeCount, long dislikeCount) {
        this.likeCount = likeCount;
        this.dislikeCount = dislikeCount;
    }

    // Getters
    public long getLikeCount() { return likeCount; }
    public long getDislikeCount() { return dislikeCount; }
}
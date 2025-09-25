package com.example.blog.dto;

public class NewCommentRequest {

    public NewCommentRequest() {
    }

    private Long postId;
    private String content;

    public Long getPostId() {
        return postId;
    }

    // public void setPost(Post post) {
    //     this.post = post;
    // }

    public String getContent() {
        return content;
    }

    // public void setContent(String content) {
    //     this.content = content;
    // }
}

package com.example.blog.model;

import lombok.Data;

@Data
public class JwtResponse {

    private String accessToken;
    private String refreshToken;
    private boolean isAdmin;

    public JwtResponse(String accessToken, String refreshToken, boolean isAdmin) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.isAdmin = isAdmin;
    }

    // public String getAccessToken() {
    //     return this.accessToken;
    // }

    // public String getRefreshToken() {
    //     return this.refreshToken;
    // }

    // public void setAccessToken(String accessToken) {
    //     this.accessToken = accessToken;
    // }

    // public void setRefreshToken(String refreshToken) {
    //     this.refreshToken = refreshToken;
    // }

}

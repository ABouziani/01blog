package com.example.blog.model;


public class JwtResponse {

    private String accessToken;
    private String refreshToken;
    private boolean isAdmin;

    public JwtResponse(String accessToken, String refreshToken, boolean isAdmin) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.isAdmin = isAdmin;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

}

package com.example.demon.dto;

public class AuthResponse {
    private String token;
    private String refreshToken;
    private String message;

    public AuthResponse() {
    }

    public AuthResponse(String token, String refreshToken, String message) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

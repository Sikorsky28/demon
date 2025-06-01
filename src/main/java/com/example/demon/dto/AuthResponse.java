package com.example.demon.dto;

public class AuthResponse {
    private String token;
    private String message;

    // Конструктор для токена и сообщения
    public AuthResponse(String token, String message) {
        this.token = token;
        this.message = message;
    }

    // Конструктор только для сообщения (для ошибок или успеха без токена)
    public AuthResponse(String message) {
        this.token = null;
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
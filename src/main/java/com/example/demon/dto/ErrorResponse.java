package com.example.demon.dto;

import java.io.Serializable;

public class ErrorResponse implements Serializable {
    private String message;

    public ErrorResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
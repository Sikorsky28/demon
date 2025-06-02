package com.example.demon.dto;

import java.io.Serializable;

public class UserResponse implements Serializable {
    private String username;
    private int age;
    private double weight;
    private String activityLevel;
    private String message; // Добавлено для сообщений об ошибках

    // Конструктор для успешного ответа
    public UserResponse(String username, int age, double weight, String activityLevel) {
        this.username = username;
        this.age = age;
        this.weight = weight;
        this.activityLevel = activityLevel;
        this.message = null;
    }

    // Конструктор для ответа об ошибке
    public UserResponse(String message) {
        this.username = null;
        this.age = 0;
        this.weight = 0.0;
        this.activityLevel = null;
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getActivityLevel() {
        return activityLevel;
    }

    public void setActivityLevel(String activityLevel) {
        this.activityLevel = activityLevel;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
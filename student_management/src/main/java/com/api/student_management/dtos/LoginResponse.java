package com.api.student_management.dtos;

public class LoginResponse {
    private String token;

    private long expiresIn;

    public String getToken() {
        return token;
    }

//    // Getters and setters...
    public LoginResponse setToken(String token) {
        this.token = token;
        return this;
    }
//    public long getExpiresIn() {
//        return expiresIn;
//    }
    public LoginResponse setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
        return this;
    }
//    public LoginResponse() {
//    }
//
//    public LoginResponse(String token, long expiresIn) {
//        this.token = token;
//        this.expiresIn = expiresIn;
//    }
//
}
package com.api.student_management.dtos;

//import lombok.Getter;

public class LoginResponse {
//    @Getter
    private String token;

    private long expiresIn;

    //    // Getters and setters...
    public String getToken() {
        return token;
    }
    public long getExpiresIn() {
        return expiresIn;
    }
    public void setToken(String token) {
        this.token = token;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
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
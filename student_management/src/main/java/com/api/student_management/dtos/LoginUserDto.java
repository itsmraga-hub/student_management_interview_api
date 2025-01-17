package com.api.student_management.dtos;

public class LoginUserDto {
    private String email;

    private String password;

    // getters and setters here...
    public String getEmail(){
        return this.email;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public String getPassword(){
        return this.password;
    }
    public void setPassword(String password){
        this.password = password;
    }
}
package com.example.proyectforeingfood;

public class User {

    private String userId;
    private String username;
    private String email;
    private String nationality;

    public User() {
        // Constructor vacío requerido por Firebase
    }

    public User(String userId, String username, String email, String nationality) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.nationality = nationality;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }
}

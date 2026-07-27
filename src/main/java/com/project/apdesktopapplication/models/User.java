package com.project.apdesktopapplication.models;

public class User {
    private String userId;
    private String username;
    private String fullName;
    private String password;
    private String role; // STUDENT, STAFF, ADMIN
    private String status; // ACTIVE, INACTIVE

    public User() {}

    public User(String userId, String username, String fullName, String password, String role, String status) {
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.password = password;
        this.role = role;
        this.status = status;
    }

    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return userId + "|" + username + "|" + fullName + "|" + password + "|" + role + "|" + status;
    }

    public static User fromString(String line) {
        String[] parts = line.split("\\|");
        return new User(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5]);
    }
}
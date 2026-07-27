package com.project.apdesktopapplication.models;

// Plain data model for a user account. userId, username, fullName, password
// (hashed by PasswordHasher before it ever reaches here), role (STUDENT/STAFF/ADMIN)
// and status (ACTIVE/INACTIVE) - matches what UserService persists to users.txt
// and what every controller (Register, Login, AdminManageUsers) reads/writes.
public class User {

    private String userId;
    private String username;
    private String fullName;
    private String password; // stored as a hash, never plain text
    private String role;
    private String status;

    public User(String userId, String username, String fullName, String password, String role, String status) {
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.password = password;
        this.role = role;
        this.status = status;
    }

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

    // Parses one line of users.txt back into a User. Field order must match toString().
    public static User fromString(String line) {
        if (line == null || line.isBlank()) return null;
        String[] p = line.split("\\|", -1);
        if (p.length < 6) return null;
        return new User(p[0], p[1], p[2], p[3], p[4], p[5]);
    }

    // One pipe-delimited line per user, written to users.txt.
    @Override
    public String toString() {
        return String.join("|", userId, username, fullName, password, role, status);
    }
}
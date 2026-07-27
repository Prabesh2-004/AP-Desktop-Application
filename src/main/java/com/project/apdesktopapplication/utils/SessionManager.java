package com.project.apdesktopapplication.utils;

import com.project.apdesktopapplication.models.User;

public class SessionManager {
    private static SessionManager instance;
    private User currentUser;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public String getRole() {
        return currentUser != null ? currentUser.getRole() : null;
    }

    public void logout() {
        currentUser = null;
    }
}
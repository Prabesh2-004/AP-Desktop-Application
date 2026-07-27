package com.project.apdesktopapplication.services;

import com.project.apdesktopapplication.models.User;
import com.project.apdesktopapplication.utils.DataManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserService {
    private List<User> users;
    private static UserService instance;

    private UserService() {
        loadUsers();
    }

    public static UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    private void loadUsers() {
        users = new ArrayList<>();
        List<String> lines = DataManager.readUsers();
        if (lines.isEmpty()) {
            // Create default admin if no users exist
            createDefaultAdmin();
        } else {
            for (String line : lines) {
                users.add(User.fromString(line));
            }
        }
    }

    private void createDefaultAdmin() {
        User admin = new User("ADMIN001", "admin", "System Admin", "admin123", "ADMIN", "ACTIVE");
        users.add(admin);
        saveUsers();
    }

    public void saveUsers() {
        List<String> lines = new ArrayList<>();
        for (User user : users) {
            lines.add(user.toString());
        }
        DataManager.writeUsers(lines);
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    public User getUserById(String userId) {
        for (User user : users) {
            if (user.getUserId().equals(userId)) {
                return user;
            }
        }
        return null;
    }

    public User getUserByUsername(String username) {
        for (User user : users) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                return user;
            }
        }
        return null;
    }

    public User authenticate(String username, String password) {
        User user = getUserByUsername(username);
        if (user != null && user.getPassword().equals(password) && user.getStatus().equals("ACTIVE")) {
            return user;
        }
        return null;
    }

    public boolean addUser(User user) {
        if (getUserByUsername(user.getUsername()) != null) {
            return false;
        }
        if (user.getUserId() == null || user.getUserId().isEmpty()) {
            user.setUserId("USR" + System.currentTimeMillis());
        }
        users.add(user);
        saveUsers();
        return true;
    }

    public boolean updateUser(User user) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUserId().equals(user.getUserId())) {
                users.set(i, user);
                saveUsers();
                return true;
            }
        }
        return false;
    }

    public boolean deleteUser(String userId) {
        User user = getUserById(userId);
        if (user != null && !user.getRole().equals("ADMIN")) {
            users.remove(user);
            saveUsers();
            return true;
        }
        return false;
    }

    public long getTotalUsers() {
        return users.size();
    }

    public long getActiveUsers() {
        return users.stream().filter(u -> u.getStatus().equals("ACTIVE")).count();
    }
}
package com.project.apdesktopapplication.services;

import com.project.apdesktopapplication.generics.ResourceManager;
import com.project.apdesktopapplication.models.User;
import com.project.apdesktopapplication.utils.DataManager;
import com.project.apdesktopapplication.utils.PasswordHasher;

import java.util.ArrayList;
import java.util.List;

/**
 * Business layer for user accounts. Data is held in a generic
 * ResourceManager<User> and persisted to users.txt via DataManager.
 * Implemented as a Singleton so every controller shares one authoritative
 * in-memory copy of the user list.
 */
public class UserService {

    private final ResourceManager<User> users = new ResourceManager<>();
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
        List<String> lines = DataManager.readUsers();
        if (lines.isEmpty()) {
            createDefaultAdmin();
        } else {
            for (String line : lines) {
                User u = User.fromString(line);
                if (u != null) users.add(u);
            }
        }
    }

    private void createDefaultAdmin() {
        // Default admin password is hashed too, so authenticate() below works for it as well.
        User admin = User.create("ADMIN001", "admin", "System Admin",
                PasswordHasher.hash("admin123"), "ADMIN", "ACTIVE");
        users.add(admin);
        saveUsers();
    }

    public void saveUsers() {
        List<String> lines = new ArrayList<>();
        for (User user : users.getAll()) {
            lines.add(user.toString());
        }
        DataManager.writeUsers(lines);
    }

    public List<User> getAllUsers() {
        return users.getAll();
    }

    public User getUserById(String userId) {
        return users.getById(userId);
    }

    public User getUserByUsername(String username) {
        return users.findAll(u -> u.getUsername().equalsIgnoreCase(username))
                .stream().findFirst().orElse(null);
    }

    public User authenticate(String username, String rawPassword) {
        User user = getUserByUsername(username);
        // Hash the attempt and compare against the stored hash - passwords are
        // never stored or compared as plain text.
        if (user != null && PasswordHasher.matches(rawPassword, user.getPassword())
                && user.getStatus().equals("ACTIVE")) {
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
        boolean ok = users.update(user);
        if (ok) saveUsers();
        return ok;
    }

    public boolean deleteUser(String userId) {
        User user = getUserById(userId);
        if (user != null && !user.getRole().equals("ADMIN")) {
            users.removeById(userId);
            saveUsers();
            return true;
        }
        return false;
    }

    public long getTotalUsers() {
        return users.size();
    }

    public long getActiveUsers() {
        return users.count(u -> u.getStatus().equals("ACTIVE"));
    }
}

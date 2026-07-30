package com.project.apdesktopapplication.models;

import com.project.apdesktopapplication.generics.Identifiable;

/**
 * Abstract base of the user hierarchy.
 * <p>
 * A {@code User} owns the state common to every account - id, username,
 * full name, password (always stored as a hash, never plain text), role and
 * status. What a user is <em>allowed</em> to do is deliberately left abstract:
 * each concrete subclass ({@link Student}, {@link Staff}, {@link Admin})
 * answers the permission questions its own way. Controllers call
 * {@code user.canApproveBookings()} without caring which subclass they hold -
 * that is runtime polymorphism enforcing role-based access control.
 * <p>
 * The persisted {@code role} string is kept so the flat file format is
 * unchanged and so existing screens that read {@code getRole()} keep working.
 */
public abstract class User implements Identifiable {

    private String userId;
    private String username;
    private String fullName;
    private String password; // stored as a SHA-256 hash, never plain text
    private String role;     // STUDENT / STAFF / ADMIN
    private String status;   // ACTIVE / INACTIVE

    protected User(String userId, String username, String fullName,
                   String password, String role, String status) {
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.password = password;
        this.role = role;
        this.status = status;
    }

    // ---- Polymorphic permission API: each subclass answers differently ----

    /** True only for roles allowed to approve or reject bookings. */
    public abstract boolean canApproveBookings();

    /** True only for roles with full system control (manage users/resources). */
    public abstract boolean canManageSystem();

    // ---- Identifiable ----
    @Override
    public String getId() { return userId; }

    // ---- Encapsulated accessors ----
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

    /**
     * Factory that instantiates the correct subclass from a role string.
     * This is the single place that maps a role to a concrete type, so callers
     * (registration, admin user management, file loading) never need a switch of
     * their own.
     */
    public static User create(String userId, String username, String fullName,
                              String password, String role, String status) {
        if (role == null) role = "STUDENT";
        switch (role.toUpperCase()) {
            case "ADMIN": return new Admin(userId, username, fullName, password, status);
            case "STAFF": return new Staff(userId, username, fullName, password, status);
            default:      return new Student(userId, username, fullName, password, status);
        }
    }

    /** Parses one pipe-delimited line of users.txt back into the right subclass. */
    public static User fromString(String line) {
        if (line == null || line.isBlank()) return null;
        String[] p = line.split("\\|", -1);
        if (p.length < 6) return null;
        return create(p[0], p[1], p[2], p[3], p[4], p[5]);
    }

    /** One pipe-delimited line per user, written to users.txt. Field order matches fromString(). */
    @Override
    public String toString() {
        return String.join("|", userId, username, fullName, password, role, status);
    }
}

package com.project.apdesktopapplication.models;

/**
 * An administrator account with full control: approve/reject any booking and
 * manage users and resources. Both permission checks return true.
 */
public class Admin extends User {
    public Admin(String userId, String username, String fullName, String password, String status) {
        super(userId, username, fullName, password, "ADMIN", status);
    }

    @Override
    public boolean canApproveBookings() { return true; }

    @Override
    public boolean canManageSystem() { return true; }
}

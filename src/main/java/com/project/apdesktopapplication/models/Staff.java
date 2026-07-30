package com.project.apdesktopapplication.models;

/**
 * A staff account. Staff inherit everything a student can do and additionally
 * may approve or reject bookings for the resources they own - hence
 * canApproveBookings() is true. They are not full system administrators.
 */
public class Staff extends User {
    public Staff(String userId, String username, String fullName, String password, String status) {
        super(userId, username, fullName, password, "STAFF", status);
    }

    @Override
    public boolean canApproveBookings() { return true; }

    @Override
    public boolean canManageSystem() { return false; }
}

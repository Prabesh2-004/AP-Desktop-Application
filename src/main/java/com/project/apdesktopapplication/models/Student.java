package com.project.apdesktopapplication.models;

/**
 * A student account. Students may search resources and request their own
 * bookings, but cannot approve bookings or manage the system - so both
 * permission checks return false.
 */
public class Student extends User {
    public Student(String userId, String username, String fullName, String password, String status) {
        super(userId, username, fullName, password, "STUDENT", status);
    }

    @Override
    public boolean canApproveBookings() { return false; }

    @Override
    public boolean canManageSystem() { return false; }
}

package com.project.apdesktopapplication.models;

/** A concrete resource type. Overrides the booking policy for this category. */
public class LabResource extends Resource {
    public LabResource(String resourceId, String name, String type, String location,
                  int capacity, String status, String creatorId) {
        super(resourceId, name, type, location, capacity, status, creatorId);
    }

    @Override
    public int getMaxBookingHours() { return 3; }

    @Override
    public String getBookingRules() { return "Labs may be booked for up to 3 hours per session."; }
}

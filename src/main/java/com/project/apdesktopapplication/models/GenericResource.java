package com.project.apdesktopapplication.models;

/** A concrete resource type. Overrides the booking policy for this category. */
public class GenericResource extends Resource {
    public GenericResource(String resourceId, String name, String type, String location,
                  int capacity, String status, String creatorId) {
        super(resourceId, name, type, location, capacity, status, creatorId);
    }

    @Override
    public int getMaxBookingHours() { return 4; }

    @Override
    public String getBookingRules() { return "This resource may be booked for up to 4 hours per session."; }
}

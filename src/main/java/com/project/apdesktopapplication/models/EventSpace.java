package com.project.apdesktopapplication.models;

/** A concrete resource type. Overrides the booking policy for this category. */
public class EventSpace extends Resource {
    public EventSpace(String resourceId, String name, String type, String location,
                  int capacity, String status, String creatorId) {
        super(resourceId, name, type, location, capacity, status, creatorId);
    }

    @Override
    public int getMaxBookingHours() { return 8; }

    @Override
    public String getBookingRules() { return "Event spaces may be booked for up to 8 hours per session."; }
}

package com.project.apdesktopapplication.models;

/** A concrete resource type. Overrides the booking policy for this category. */
public class EquipmentResource extends Resource {
    public EquipmentResource(String resourceId, String name, String type, String location,
                  int capacity, String status, String creatorId) {
        super(resourceId, name, type, location, capacity, status, creatorId);
    }

    @Override
    public int getMaxBookingHours() { return 2; }

    @Override
    public String getBookingRules() { return "Equipment may be borrowed for up to 2 hours per session."; }
}

package com.project.apdesktopapplication.models;

import com.project.apdesktopapplication.generics.Identifiable;

// A booking record. Implements Identifiable (getId -> bookingId) so it can be
// stored and looked up by the generic ResourceManager<T> like every other entity.
public class Booking implements Identifiable {
    private String bookingId;
    private String userId;
    private String resourceId;
    private String date;
    private String startTime;
    private String endTime;
    private String status; // PENDING, APPROVED, REJECTED, CANCELLED

    public Booking() {}

    public Booking(String bookingId, String userId, String resourceId, String date, String startTime, String endTime, String status) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.resourceId = resourceId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }

    // Identifiable
    @Override
    public String getId() { return bookingId; }

    // Getters and Setters
    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getResourceId() { return resourceId; }
    public void setResourceId(String resourceId) { this.resourceId = resourceId; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return bookingId + "|" + userId + "|" + resourceId + "|" + date + "|" + startTime + "|" + endTime + "|" + status;
    }

    public static Booking fromString(String line) {
        String[] parts = line.split("\\|");
        return new Booking(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], parts[6]);
    }
}
package com.project.apdesktopapplication.models;

public class Resource {
    private String resourceId;
    private String name;
    private String type;
    private String location;
    private int capacity;
    private String status; // AVAILABLE, MAINTENANCE, BOOKED
    private String creatorId;

    public Resource() {}

    public Resource(String resourceId, String name, String type, String location, int capacity, String status, String creatorId) {
        this.resourceId = resourceId;
        this.name = name;
        this.type = type;
        this.location = location;
        this.capacity = capacity;
        this.status = status;
        this.creatorId = creatorId;
    }

    // Getters and Setters
    public String getResourceId() { return resourceId; }
    public void setResourceId(String resourceId) { this.resourceId = resourceId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCreatorId() { return creatorId; }
    public void setCreatorId(String creatorId) { this.creatorId = creatorId; }

    @Override
    public String toString() {
        return resourceId + "|" + name + "|" + type + "|" + location + "|" + capacity + "|" + status + "|" + creatorId;
    }

    public static Resource fromString(String line) {
        String[] parts = line.split("\\|");
        return new Resource(parts[0], parts[1], parts[2], parts[3], Integer.parseInt(parts[4]), parts[5], parts[6]);
    }
}
package com.project.apdesktopapplication;

import java.util.UUID;

public class Resource {
    private String resourceId;
    private String name;
    private String location;
    private String type;
    private String capacity;
    private String status;
    private String creatorName;

    public Resource(String resourceId, String name, String location, String type, String capacity, String status, String creatorName) {
        this.resourceId = resourceId;
        this.name = name;
        this.location = location;
        this.type = type;
        this.capacity = capacity;
        this.status = status;
        this.creatorName = creatorName;
    }

    // Constructor without location for admin management
    public Resource(String resourceId, String name, String type, String capacity, String status, String creatorName) {
        this(resourceId, name, "", type, capacity, status, creatorName);
    }

    public String getResourceId() { return resourceId; }
    public String getName() { return name; }
    public String getLocation() { return location; }
    public String getType() { return type; }
    public String getCapacity() { return capacity; }
    public String getStatus() { return status; }
    public String getCreatorName() { return creatorName; }

    // Setters for admin management
    public void setName(String name) { this.name = name; }
    public void setType(String type) { this.type = type; }
    public void setCapacity(String capacity) { this.capacity = capacity; }
    public void setStatus(String status) { this.status = status; }
}
package com.project.apdesktopapplication.models;

import com.project.apdesktopapplication.generics.Identifiable;

/**
 * Abstract base of the resource hierarchy.
 * <p>
 * All resources share the same attributes (id, name, type, location, capacity,
 * status, creator), but each <em>type</em> of resource enforces its own booking
 * policy. Rather than scatter {@code if (type.equals("Lab")) ...} checks through
 * the code, the policy is expressed polymorphically: {@link #getMaxBookingHours()}
 * and {@link #getBookingRules()} are abstract, and each subclass supplies its own
 * answer. The booking validator simply asks the resource for its limit and trusts
 * the right subclass to respond.
 */
public abstract class Resource implements Identifiable {

    private String resourceId;
    private String name;
    private String type;
    private String location;
    private int capacity;
    private String status; // AVAILABLE, MAINTENANCE, BOOKED
    private String creatorId;

    protected Resource() {}

    protected Resource(String resourceId, String name, String type, String location,
                       int capacity, String status, String creatorId) {
        this.resourceId = resourceId;
        this.name = name;
        this.type = type;
        this.location = location;
        this.capacity = capacity;
        this.status = status;
        this.creatorId = creatorId;
    }

    // ---- Polymorphic booking policy: each subclass overrides these ----

    /** Maximum number of hours a single booking of this resource type may last. */
    public abstract int getMaxBookingHours();

    /** Short, human-readable description of this resource type's booking rule. */
    public abstract String getBookingRules();

    // ---- Identifiable ----
    @Override
    public String getId() { return resourceId; }

    // ---- Encapsulated accessors ----
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

    /**
     * Factory that builds the concrete subclass matching the type string.
     * Unknown types fall back to a {@link GenericResource} so loading never fails.
     */
    public static Resource create(String resourceId, String name, String type, String location,
                                  int capacity, String status, String creatorId) {
        String key = type == null ? "" : type.trim().toLowerCase();
        switch (key) {
            case "study room":  return new StudyRoom(resourceId, name, type, location, capacity, status, creatorId);
            case "lab":         return new LabResource(resourceId, name, type, location, capacity, status, creatorId);
            case "event space": return new EventSpace(resourceId, name, type, location, capacity, status, creatorId);
            case "equipment":   return new EquipmentResource(resourceId, name, type, location, capacity, status, creatorId);
            case "meeting room":return new MeetingRoom(resourceId, name, type, location, capacity, status, creatorId);
            default:            return new GenericResource(resourceId, name, type, location, capacity, status, creatorId);
        }
    }

    @Override
    public String toString() {
        return resourceId + "|" + name + "|" + type + "|" + location + "|" + capacity + "|" + status + "|" + creatorId;
    }

    /** Parses one pipe-delimited line of resources.txt into the correct subclass. */
    public static Resource fromString(String line) {
        String[] p = line.split("\\|");
        return create(p[0], p[1], p[2], p[3], Integer.parseInt(p[4]), p[5], p[6]);
    }
}

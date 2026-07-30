package com.project.apdesktopapplication.services;

import com.project.apdesktopapplication.generics.ResourceManager;
import com.project.apdesktopapplication.models.Resource;
import com.project.apdesktopapplication.utils.DataManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Business layer for resources. Backed by a generic ResourceManager<Resource>
 * and persisted to resources.txt. Singleton so all screens share one copy.
 * Resource objects are created through Resource.create(...), which returns the
 * correct subclass (StudyRoom, LabResource, EventSpace, ...) for each type.
 */
public class ResourceService {

    private final ResourceManager<Resource> resources = new ResourceManager<>();
    private static ResourceService instance;

    private ResourceService() {
        loadResources();
    }

    public static ResourceService getInstance() {
        if (instance == null) {
            instance = new ResourceService();
        }
        return instance;
    }

    private void loadResources() {
        List<String> lines = DataManager.readResources();
        if (lines.isEmpty()) {
            createDefaultResources();
        } else {
            for (String line : lines) {
                if (line.trim().isEmpty()) continue;
                try {
                    resources.add(Resource.fromString(line));
                } catch (Exception e) {
                    System.err.println("Error parsing resource: " + line);
                }
            }
        }
        System.out.println("Loaded " + resources.size() + " resources from file");
    }

    private void createDefaultResources() {
        resources.add(Resource.create("RES001", "Conference Room A", "Meeting Room", "Building A - Floor 1", 20, "AVAILABLE", "ADMIN001"));
        resources.add(Resource.create("RES002", "Computer Lab", "Lab", "Building B - Floor 2", 30, "AVAILABLE", "ADMIN001"));
        resources.add(Resource.create("RES003", "Auditorium", "Event Space", "Building C - Floor 1", 100, "MAINTENANCE", "ADMIN001"));
        resources.add(Resource.create("RES004", "Study Room 1", "Study Room", "Library - Floor 1", 4, "AVAILABLE", "ADMIN001"));
        resources.add(Resource.create("RES005", "Study Room 2", "Study Room", "Library - Floor 2", 6, "AVAILABLE", "ADMIN001"));
        resources.add(Resource.create("RES006", "Projector Setup", "Equipment", "Building A - Floor 2", 1, "AVAILABLE", "ADMIN001"));
        saveResources();
        System.out.println("Created default resources");
    }

    public void saveResources() {
        List<String> lines = new ArrayList<>();
        for (Resource resource : resources.getAll()) {
            lines.add(resource.toString());
        }
        DataManager.writeResources(lines);
        System.out.println("Saved " + resources.size() + " resources to file");
    }

    public List<Resource> getAllResources() {
        return resources.getAll();
    }

    public Resource getResourceById(String resourceId) {
        return resources.getById(resourceId);
    }

    public List<Resource> getResourcesByStatus(String status) {
        return resources.findAll(r -> r.getStatus().equals(status));
    }

    public List<Resource> getAvailableResources() {
        return resources.findAll(r -> r.getStatus().equals("AVAILABLE"));
    }

    public boolean addResource(Resource resource) {
        if (resource.getResourceId() == null || resource.getResourceId().isEmpty()) {
            resource.setResourceId("RES" + String.format("%03d", resources.size() + 1));
        }
        resources.add(resource);
        saveResources();
        return true;
    }

    public boolean updateResource(Resource resource) {
        boolean ok = resources.update(resource);
        if (ok) saveResources();
        return ok;
    }

    public boolean deleteResource(String resourceId) {
        boolean ok = resources.removeById(resourceId);
        if (ok) saveResources();
        return ok;
    }

    public long getTotalResources() {
        return resources.size();
    }

    public long getAvailableResourcesCount() {
        return resources.count(r -> r.getStatus().equals("AVAILABLE"));
    }

    public long getMaintenanceResourcesCount() {
        return resources.count(r -> r.getStatus().equals("MAINTENANCE"));
    }

    public List<String> getAllTypes() {
        return resources.getAll().stream().map(Resource::getType).distinct().collect(Collectors.toList());
    }

    public List<String> getAllLocations() {
        return resources.getAll().stream().map(Resource::getLocation).distinct().collect(Collectors.toList());
    }
}

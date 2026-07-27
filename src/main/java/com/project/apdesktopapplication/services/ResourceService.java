package com.project.apdesktopapplication.services;

import com.project.apdesktopapplication.models.Resource;
import com.project.apdesktopapplication.utils.DataManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ResourceService {
    private List<Resource> resources;
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
        resources = new ArrayList<>();
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
        resources.add(new Resource("RES001", "Conference Room A", "Meeting Room", "Building A - Floor 1", 20, "AVAILABLE", "ADMIN001"));
        resources.add(new Resource("RES002", "Computer Lab", "Lab", "Building B - Floor 2", 30, "AVAILABLE", "ADMIN001"));
        resources.add(new Resource("RES003", "Auditorium", "Event Space", "Building C - Floor 1", 100, "MAINTENANCE", "ADMIN001"));
        resources.add(new Resource("RES004", "Study Room 1", "Study Room", "Library - Floor 1", 4, "AVAILABLE", "ADMIN001"));
        resources.add(new Resource("RES005", "Study Room 2", "Study Room", "Library - Floor 2", 6, "AVAILABLE", "ADMIN001"));
        resources.add(new Resource("RES006", "Projector Setup", "Equipment", "Building A - Floor 2", 1, "AVAILABLE", "ADMIN001"));
        saveResources();
        System.out.println("Created default resources");
    }

    public void saveResources() {
        List<String> lines = new ArrayList<>();
        for (Resource resource : resources) {
            lines.add(resource.toString());
        }
        DataManager.writeResources(lines);
        System.out.println("Saved " + resources.size() + " resources to file");
    }

    public List<Resource> getAllResources() {
        return new ArrayList<>(resources);
    }

    public Resource getResourceById(String resourceId) {
        for (Resource resource : resources) {
            if (resource.getResourceId().equals(resourceId)) {
                return resource;
            }
        }
        return null;
    }

    public List<Resource> getResourcesByStatus(String status) {
        return resources.stream()
                .filter(r -> r.getStatus().equals(status))
                .collect(Collectors.toList());
    }

    public List<Resource> getAvailableResources() {
        return resources.stream()
                .filter(r -> r.getStatus().equals("AVAILABLE"))
                .collect(Collectors.toList());
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
        for (int i = 0; i < resources.size(); i++) {
            if (resources.get(i).getResourceId().equals(resource.getResourceId())) {
                resources.set(i, resource);
                saveResources();
                return true;
            }
        }
        return false;
    }

    public boolean deleteResource(String resourceId) {
        Resource resource = getResourceById(resourceId);
        if (resource != null) {
            resources.remove(resource);
            saveResources();
            return true;
        }
        return false;
    }

    public long getTotalResources() {
        return resources.size();
    }

    public long getAvailableResourcesCount() {
        return resources.stream().filter(r -> r.getStatus().equals("AVAILABLE")).count();
    }

    public long getMaintenanceResourcesCount() {
        return resources.stream().filter(r -> r.getStatus().equals("MAINTENANCE")).count();
    }

    public List<String> getAllTypes() {
        return resources.stream().map(Resource::getType).distinct().collect(Collectors.toList());
    }

    public List<String> getAllLocations() {
        return resources.stream().map(Resource::getLocation).distinct().collect(Collectors.toList());
    }
}
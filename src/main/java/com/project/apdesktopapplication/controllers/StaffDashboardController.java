package com.project.apdesktopapplication.controllers;

import com.project.apdesktopapplication.models.Booking;
import com.project.apdesktopapplication.models.Resource;
import com.project.apdesktopapplication.models.User;
import com.project.apdesktopapplication.services.BookingService;
import com.project.apdesktopapplication.services.ResourceService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.util.List;
import java.util.stream.Collectors;

public class StaffDashboardController {
    @FXML private Label myResourcesLabel;
    @FXML private Label totalBookingsLabel;
    @FXML private Label pendingApprovalsLabel;
    @FXML private Label availableResourcesLabel;
    @FXML private Button addResourceBtn;
    @FXML private Button viewPendingBtn;

    private User currentUser;
    private ResourceService resourceService = ResourceService.getInstance();
    private BookingService bookingService = BookingService.getInstance();
    private StaffNavigationController navigationController;

    public void setCurrentUser(User user) {
        this.currentUser = user;
        refreshData();
    }

    public void setNavigationController(StaffNavigationController nav) {
        this.navigationController = nav;
    }

    @FXML
    public void initialize() {
        // Button actions will be set when navigation controller is available
    }

    @FXML
    private void handleAddResource() {
        if (navigationController != null) {
            navigationController.showMyResources();
        }
    }

    @FXML
    private void handleViewPending() {
        if (navigationController != null) {
            navigationController.showPendingApprovals();
        }
    }

    public void refreshData() {
        if (currentUser == null) return;

        // Get staff's resources
        List<Resource> myResources = resourceService.getAllResources().stream()
                .filter(r -> r.getCreatorId().equals(currentUser.getUserId()))
                .collect(Collectors.toList());

        // Get bookings for staff's resources
        List<String> myResourceIds = myResources.stream()
                .map(Resource::getResourceId)
                .collect(Collectors.toList());

        List<Booking> myBookings = bookingService.getAllBookings().stream()
                .filter(b -> myResourceIds.contains(b.getResourceId()))
                .collect(Collectors.toList());

        long pendingCount = myBookings.stream()
                .filter(b -> "PENDING".equals(b.getStatus()))
                .count();

        long availableCount = myResources.stream()
                .filter(r -> "AVAILABLE".equals(r.getStatus()))
                .count();

        myResourcesLabel.setText(String.valueOf(myResources.size()));
        totalBookingsLabel.setText(String.valueOf(myBookings.size()));
        pendingApprovalsLabel.setText(String.valueOf(pendingCount));
        availableResourcesLabel.setText(String.valueOf(availableCount));
    }
}
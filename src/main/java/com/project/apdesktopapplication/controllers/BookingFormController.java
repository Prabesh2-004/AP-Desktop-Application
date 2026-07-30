package com.project.apdesktopapplication.controllers;

import com.project.apdesktopapplication.exceptions.InvalidBookingDurationException;
import com.project.apdesktopapplication.exceptions.ResourceUnavailableException;
import com.project.apdesktopapplication.models.Booking;
import com.project.apdesktopapplication.models.Resource;
import com.project.apdesktopapplication.models.User;
import com.project.apdesktopapplication.services.BookingService;
import com.project.apdesktopapplication.services.ResourceService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class BookingFormController {
    @FXML private Label resourceNameLabel;
    @FXML private Label resourceIdLabel;
    @FXML private Label resourceLocationLabel;
    @FXML private Label resourceCapacityLabel;
    @FXML private Label resourceCreatorLabel;
    @FXML private Label bookingStatusLabel;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> startTimeBox;
    @FXML private ComboBox<String> endTimeBox;
    @FXML private Label durationLabel;
    @FXML private Label messageLabel;
    @FXML private Button confirmButton;
    @FXML private Button cancelButton;

    private User currentUser;
    private Resource resource;
    private BookingService bookingService = BookingService.getInstance();
    private ResourceService resourceService = ResourceService.getInstance();
    private Runnable onBookingComplete;

    @FXML
    public void initialize() {
        // Populate time dropdowns
        for (int hour = 7; hour <= 21; hour++) {
            String time = String.format("%02d:00", hour);
            startTimeBox.getItems().add(time);
            endTimeBox.getItems().add(time);
        }

        // Set default date to today
        datePicker.setValue(LocalDate.now());

        // Add listeners to calculate duration
        startTimeBox.setOnAction(e -> calculateDuration());
        endTimeBox.setOnAction(e -> calculateDuration());
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (user != null) {
            String role = user.getRole();
            if ("ADMIN".equals(role) || "STAFF".equals(role)) {
                bookingStatusLabel.setText("✅ Auto-Approved");
                bookingStatusLabel.setStyle("-fx-text-fill: #16A34A; -fx-font-weight: bold;");
            } else {
                bookingStatusLabel.setText("⏳ Pending Approval");
                bookingStatusLabel.setStyle("-fx-text-fill: #EAB308; -fx-font-weight: bold;");
            }
        }
    }

    public void setResource(Resource resource) {
        this.resource = resource;
        if (resource != null) {
            resourceNameLabel.setText(resource.getName());
            resourceIdLabel.setText("ID: " + resource.getResourceId());
            resourceLocationLabel.setText("📍 " + resource.getLocation());
            resourceCapacityLabel.setText("👥 " + resource.getCapacity() + " people");
            resourceCreatorLabel.setText(resource.getCreatorId());
        }
    }

    public void setOnBookingComplete(Runnable callback) {
        this.onBookingComplete = callback;
    }

    private void calculateDuration() {
        String start = startTimeBox.getValue();
        String end = endTimeBox.getValue();

        if (start != null && end != null) {
            try {
                LocalTime startTime = LocalTime.parse(start);
                LocalTime endTime = LocalTime.parse(end);
                if (endTime.isAfter(startTime)) {
                    long hours = java.time.Duration.between(startTime, endTime).toHours();
                    if (hours == 0) {
                        durationLabel.setText("⏱ Duration: 1 hour (minimum)");
                    } else {
                        durationLabel.setText("⏱ Duration: " + hours + " hour" + (hours > 1 ? "s" : ""));
                    }
                } else {
                    durationLabel.setText("⏱ Duration: Invalid time range");
                }
            } catch (Exception e) {
                durationLabel.setText("⏱ Duration: -");
            }
        }
    }

    @FXML
    private void handleConfirm() {
        String start = startTimeBox.getValue();
        String end = endTimeBox.getValue();
        LocalDate date = datePicker.getValue();

        if (start == null || end == null || date == null) {
            messageLabel.setText("Please select date and time");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        // Delegate all validation and creation to the service. The service throws
        // a specific custom exception for each rule it enforces; we catch them
        // individually so the user sees a meaningful message for each case.
        try {
            Resource currentResource = resourceService.getResourceById(resource.getResourceId());
            Booking booking = bookingService.createBooking(
                    currentUser, currentResource, date.toString(), start, end);

            if ("APPROVED".equals(booking.getStatus())) {
                bookingStatusLabel.setText("✅ APPROVED");
                bookingStatusLabel.setStyle("-fx-text-fill: #16A34A; -fx-font-weight: bold;");
            } else {
                bookingStatusLabel.setText("⏳ PENDING APPROVAL");
                bookingStatusLabel.setStyle("-fx-text-fill: #EAB308; -fx-font-weight: bold;");
            }

            messageLabel.setText("✓ Booking " + booking.getStatus().toLowerCase() + " successfully!");
            messageLabel.setStyle("-fx-text-fill: #16A34A;");
            confirmButton.setDisable(true);

            // Close after a short delay so the user can read the confirmation.
            new Thread(() -> {
                try {
                    Thread.sleep(1500);
                    javafx.application.Platform.runLater(() -> {
                        if (onBookingComplete != null) {
                            onBookingComplete.run();
                        }
                        Stage stage = (Stage) confirmButton.getScene().getWindow();
                        stage.close();
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (ResourceUnavailableException | InvalidBookingDurationException e) {
            // Meaningful, rule-specific feedback straight from the custom exception.
            messageLabel.setText(e.getMessage());
            messageLabel.setStyle("-fx-text-fill: red;");
        } catch (Exception e) {
            messageLabel.setText("Unexpected error: " + e.getMessage());
            messageLabel.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
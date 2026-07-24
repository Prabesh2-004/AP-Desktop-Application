package com.project.apdesktopapplication;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class BookingController {

    @FXML private Label resourceIdLabel;
    @FXML private Label resourceNameLabel;
    @FXML private Label resourceLocationLabel;
    @FXML private Label resourceCapacityLabel;
    @FXML private Label resourceCreatorLabel;
    @FXML private TextField bookingIdField;
    @FXML private TextField userIdField;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> startTimeBox;
    @FXML private ComboBox<String> endTimeBox;
    @FXML private Label durationLabel;
    @FXML private Label messageLabel;

    private Resource resource;

    @FXML
    public void initialize() {
        // Generate auto booking ID
        bookingIdField.setText(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        bookingIdField.setEditable(false);

        // Default user ID (will be replaced with actual user ID when login is implemented)
        userIdField.setText("USER-001");
        userIdField.setEditable(false);

        String[] slots = {"08:00 AM", "09:00 AM", "10:00 AM", "11:00 AM", "12:00 PM",
                "01:00 PM", "02:00 PM", "03:00 PM", "04:00 PM", "05:00 PM"};
        startTimeBox.getItems().addAll(slots);
        endTimeBox.getItems().addAll(slots);

        startTimeBox.setOnAction(e -> updateDuration());
        endTimeBox.setOnAction(e -> updateDuration());
    }

    public void setResource(Resource resource) {
        this.resource = resource;
        resourceIdLabel.setText(resource.getResourceId());
        resourceNameLabel.setText(resource.getName());
        resourceLocationLabel.setText(resource.getLocation());
        resourceCapacityLabel.setText(resource.getCapacity() + " people");
        resourceCreatorLabel.setText(resource.getCreatorName());
    }

    private void updateDuration() {
        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("hh:mm a");
            LocalTime start = LocalTime.parse(startTimeBox.getValue(), fmt);
            LocalTime end = LocalTime.parse(endTimeBox.getValue(), fmt);
            long minutes = ChronoUnit.MINUTES.between(start, end);
            if (minutes <= 0) {
                durationLabel.setText("⚠ Invalid: End time must be after start time");
                durationLabel.setStyle("-fx-text-fill: #DC2626;");
            } else {
                durationLabel.setText("⏱ " + (minutes / 60) + "h " + (minutes % 60) + "m");
                durationLabel.setStyle("-fx-text-fill: #374151;");
            }
        } catch (Exception ex) {
            durationLabel.setText("⏱ Duration: -");
            durationLabel.setStyle("-fx-text-fill: #9CA3AF;");
        }
    }

    @FXML
    private void handleConfirm() {
        if (datePicker.getValue() == null || startTimeBox.getValue() == null || endTimeBox.getValue() == null) {
            messageLabel.setStyle("-fx-text-fill: #DC2626;");
            messageLabel.setText("⚠ Please fill all fields.");
            return;
        }

        // Validate duration
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("hh:mm a");
        try {
            LocalTime start = LocalTime.parse(startTimeBox.getValue(), fmt);
            LocalTime end = LocalTime.parse(endTimeBox.getValue(), fmt);
            if (ChronoUnit.MINUTES.between(start, end) <= 0) {
                messageLabel.setStyle("-fx-text-fill: #DC2626;");
                messageLabel.setText("⚠ End time must be after start time.");
                return;
            }
        } catch (Exception e) {
            messageLabel.setStyle("-fx-text-fill: #DC2626;");
            messageLabel.setText("⚠ Invalid time format.");
            return;
        }

        messageLabel.setStyle("-fx-text-fill: #16A34A;");
        messageLabel.setText("✓ Booking confirmed! (Booking ID: " + bookingIdField.getText() + ")");
    }

    @FXML
    private void handleCancel() {
        NavigationController.goToResources();
    }
}
package com.project.apdesktopapplication.controllers;

import com.project.apdesktopapplication.models.Booking;
import com.project.apdesktopapplication.services.BookingService;
import com.project.apdesktopapplication.services.ResourceService;
import com.project.apdesktopapplication.services.UserService;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AdminDashboardController {
    @FXML public Label totalResources;
    @FXML private Label totalUsersLabel;
    @FXML private Label totalBookingsLabel;
    @FXML private Label pendingApprovalsLabel;
    @FXML private Label activeResourcesLabel;
    @FXML private Label availableResourcesLabel;
    @FXML private Label maintenanceResourcesLabel;
    @FXML private BarChart<String, Number> bookingChart;

    private UserService userService = UserService.getInstance();
    private ResourceService resourceService = ResourceService.getInstance();
    private BookingService bookingService = BookingService.getInstance();

    @FXML
    public void initialize() {
        refreshData();
    }

    public void refreshData() {
        updateStats();
        updateChart();
    }

    private void updateStats() {
        totalUsersLabel.setText(String.valueOf(userService.getTotalUsers()));
        totalBookingsLabel.setText(String.valueOf(bookingService.getTotalBookings()));
        pendingApprovalsLabel.setText(String.valueOf(bookingService.getPendingCount()));
        availableResourcesLabel.setText(String.valueOf(resourceService.getAvailableResourcesCount()));
        maintenanceResourcesLabel.setText(String.valueOf(resourceService.getMaintenanceResourcesCount()));
        activeResourcesLabel.setText(String.valueOf(resourceService.getAvailableResourcesCount()));
        totalResources.setText(String.valueOf(resourceService.getAvailableResourcesCount() + resourceService.getMaintenanceResourcesCount()));
    }

    private void updateChart() {
        bookingChart.getData().clear();

        // Get bookings for last 7 days
        List<Booking> allBookings = bookingService.getAllBookings();
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        Map<String, Long> dailyCounts = allBookings.stream()
                .filter(b -> {
                    try {
                        LocalDate bookingDate = LocalDate.parse(b.getDate(), formatter);
                        return bookingDate.isAfter(today.minusDays(8)) && bookingDate.isBefore(today.plusDays(1));
                    } catch (Exception e) {
                        return false;
                    }
                })
                .collect(Collectors.groupingBy(
                        b -> {
                            try {
                                LocalDate date = LocalDate.parse(b.getDate(), formatter);
                                return date.format(DateTimeFormatter.ofPattern("MMM dd"));
                            } catch (Exception e) {
                                return "Unknown";
                            }
                        },
                        Collectors.counting()
                ));

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Bookings");

        // Fill in all 7 days
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            String label = date.format(DateTimeFormatter.ofPattern("MMM dd"));
            long count = dailyCounts.getOrDefault(label, 0L);
            series.getData().add(new XYChart.Data<>(label, count));
        }

        bookingChart.getData().add(series);
        bookingChart.setAnimated(true);
        bookingChart.setLegendVisible(false);
    }
}
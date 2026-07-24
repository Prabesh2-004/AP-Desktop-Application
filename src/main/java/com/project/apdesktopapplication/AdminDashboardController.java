package com.project.apdesktopapplication;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class AdminDashboardController {

    @FXML private Label totalUsersLabel;
    @FXML private Label totalBookingsLabel;
    @FXML private Label pendingApprovalsLabel;
    @FXML private Label activeResourcesLabel;
    @FXML private Label availableResourcesLabel;
    @FXML private Label maintenanceResourcesLabel;
    @FXML private BarChart<String, Number> bookingChart;

    @FXML
    public void initialize() {
        updateStats();
        setupChart();
    }

    private void updateStats() {
        // Simulated data - in real app, this would come from database
        totalUsersLabel.setText("1,247");
        totalBookingsLabel.setText("3,892");
        pendingApprovalsLabel.setText("18");
        activeResourcesLabel.setText("124");
        availableResourcesLabel.setText("89");
        maintenanceResourcesLabel.setText("5");
    }

    private void setupChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Date");
        yAxis.setLabel("Bookings");

        bookingChart.setTitle("Daily Bookings (Last 7 Days)");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Daily Bookings");

        // Generate random booking data for last 7 days
        Random random = new Random();
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd");

        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            int bookings = random.nextInt(15) + 5; // Random between 5-20
            series.getData().add(new XYChart.Data<>(date.format(formatter), bookings));
        }

        bookingChart.getData().add(series);
        bookingChart.setAnimated(false);

        // Apply custom styles
        applyCustomStyles();
    }

    private void applyCustomStyles() {
        // Apply background and border styling
        bookingChart.setStyle("-fx-background-color: white; -fx-border-color: #E5E7EB; -fx-border-radius: 4;");

        // Apply bar colors using CSS string (combine both styles)
        String barStyle = ".default-color0.chart-bar { -fx-bar-fill: #1F2937; } " +
                ".chart-bar:hover { -fx-bar-fill: #374151; }";

        // Add the bar styles to the chart
        bookingChart.getStylesheets().add("data:text/css," + barStyle);

        // Alternative: Use Platform.runLater to ensure bars are rendered
        Platform.runLater(() -> {
            // Find all bars and apply colors
            for (XYChart.Series<String, Number> series : bookingChart.getData()) {
                for (XYChart.Data<String, Number> data : series.getData()) {
                    Node bar = data.getNode();
                    if (bar != null) {
                        bar.setStyle("-fx-bar-fill: #1F2937;");
                    }
                }
            }
        });
    }

    public void refreshData() {
        updateStats();
    }
}
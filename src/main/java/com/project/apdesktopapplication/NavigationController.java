package com.project.apdesktopapplication;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class NavigationController {

    @FXML private StackPane contentArea;
    @FXML private Button resourcesLink;
    @FXML private Button bookingLink;

    private static NavigationController instance;

    private static final String ACTIVE_STYLE =
            "-fx-background-color: black; -fx-text-fill: white; -fx-alignment: CENTER_LEFT; -fx-background-radius: 6; -fx-padding: 10 14 10 14;";
    private static final String INACTIVE_STYLE =
            "-fx-background-color: transparent; -fx-text-fill: #9CA3AF; -fx-alignment: CENTER_LEFT; -fx-background-radius: 6; -fx-padding: 10 14 10 14;";
    private static final String HOVER_STYLE =
            "-fx-background-color: #1F2937; -fx-text-fill: white; -fx-alignment: CENTER_LEFT; -fx-background-radius: 6; -fx-padding: 10 14 10 14;";

    @FXML
    public void initialize() {
        instance = this;
        showResources();

        // Add hover effects
        resourcesLink.setOnMouseEntered(e -> {
            if (!resourcesLink.getStyle().equals(ACTIVE_STYLE)) {
                resourcesLink.setStyle(HOVER_STYLE);
            }
        });
        resourcesLink.setOnMouseExited(e -> {
            if (!resourcesLink.getStyle().equals(ACTIVE_STYLE)) {
                resourcesLink.setStyle(INACTIVE_STYLE);
            }
        });

        bookingLink.setOnMouseEntered(e -> {
            if (!bookingLink.getStyle().equals(ACTIVE_STYLE)) {
                bookingLink.setStyle(HOVER_STYLE);
            }
        });
        bookingLink.setOnMouseExited(e -> {
            if (!bookingLink.getStyle().equals(ACTIVE_STYLE)) {
                bookingLink.setStyle(INACTIVE_STYLE);
            }
        });
    }

    @FXML
    private void showResources() {
        loadPage("resource-view.fxml");
        resourcesLink.setStyle(ACTIVE_STYLE);
        bookingLink.setStyle(INACTIVE_STYLE);
    }

    @FXML
    private void showBooking() {
        loadPage("booking-view.fxml");
        bookingLink.setStyle(ACTIVE_STYLE);
        resourcesLink.setStyle(INACTIVE_STYLE);
    }

    private void loadPage(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent page = loader.load();
            contentArea.getChildren().setAll(page);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void goToBooking(Resource resource) {
        if (instance == null) return;
        try {
            FXMLLoader loader = new FXMLLoader(instance.getClass().getResource("booking-view.fxml"));
            Parent page = loader.load();
            BookingController controller = loader.getController();
            controller.setResource(resource);
            instance.contentArea.getChildren().setAll(page);
            instance.bookingLink.setStyle(ACTIVE_STYLE);
            instance.resourcesLink.setStyle(INACTIVE_STYLE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void goToResources() {
        if (instance == null) return;
        instance.showResources();
    }
}
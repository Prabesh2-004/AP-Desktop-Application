package com.project.apdesktopapplication.controllers;

import com.project.apdesktopapplication.models.User;
import com.project.apdesktopapplication.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class NavigationController {
    @FXML private Button resourcesLink;
    @FXML private Button bookingLink;
    @FXML private Label userLabel;
    @FXML private StackPane contentArea;

    private User currentUser;

    @FXML
    public void initialize() {
        currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            userLabel.setText("👤 " + currentUser.getFullName() + " (" + currentUser.getRole() + ")");
        }
        showResources();
    }

    public void setUserInfo(User user) {
        this.currentUser = user;
        if (user != null) {
            userLabel.setText("👤 " + user.getFullName() + " (" + user.getRole() + ")");
        }
    }

    @FXML
    private void showResources() {
        loadView("/com/project/apdesktopapplication/resource-view.fxml");
        updateActiveButton(resourcesLink);
    }

    @FXML
    private void showBooking() {
        loadView("/com/project/apdesktopapplication/booking-view.fxml");
        updateActiveButton(bookingLink);
    }

    private void loadView(String fxmlPath) {
        try {
            System.out.println("Loading: " + fxmlPath);
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            if (loader.getLocation() == null) {
                System.err.println("FXML not found: " + fxmlPath);
                return;
            }
            Parent view = loader.load();

            Object controller = loader.getController();
            if (controller instanceof ResourceController) {
                ((ResourceController) controller).setCurrentUser(currentUser);
            } else if (controller instanceof BookingController) {
                ((BookingController) controller).setCurrentUser(currentUser);
            }

            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading view: " + fxmlPath);
        }
    }

    private void updateActiveButton(Button activeButton) {
        String defaultStyle = "-fx-background-color: transparent; -fx-text-fill: #9CA3AF; -fx-alignment: CENTER_LEFT; -fx-background-radius: 6; -fx-padding: 12 16 12 16; -fx-font-weight: bold;";
        resourcesLink.setStyle(defaultStyle);
        bookingLink.setStyle(defaultStyle);

        String activeStyle = "-fx-background-color: #1F2937; -fx-text-fill: white; -fx-alignment: CENTER_LEFT; -fx-background-radius: 6; -fx-padding: 12 16 12 16; -fx-font-weight: bold;";
        activeButton.setStyle(activeStyle);
    }

    @FXML
    private void logout() {
        SessionManager.getInstance().logout();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/project/apdesktopapplication/login-view.fxml"));
            Parent root = loader.load();
            javafx.stage.Stage stage = (javafx.stage.Stage) contentArea.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root, 400, 500));
            stage.setTitle("Login");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
package com.project.apdesktopapplication.controllers;

import com.project.apdesktopapplication.models.User;
import com.project.apdesktopapplication.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class StaffNavigationController {
    @FXML private Button dashboardLink;
    @FXML private Button myResourcesLink;
    @FXML private Button pendingApprovalsLink;
    @FXML private Button allResourcesLink;
    @FXML private Label staffUserLabel;
    @FXML private Label staffUserInfoLabel;
    @FXML private StackPane contentArea;

    private User currentUser;

    @FXML
    public void initialize() {
        currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            staffUserLabel.setText("👤 " + currentUser.getFullName());
            staffUserInfoLabel.setText("Role: " + currentUser.getRole() + " | ID: " + currentUser.getUserId());
        }
        showDashboard();
    }

    public void setUserInfo(User user) {
        this.currentUser = user;
        if (user != null) {
            staffUserLabel.setText("👤 " + user.getFullName());
            staffUserInfoLabel.setText("Role: " + user.getRole() + " | ID: " + user.getUserId());
        }
    }

    @FXML
    private void showDashboard() {
        loadView("/com/project/apdesktopapplication/staff-dashboard.fxml");
        updateActiveButton(dashboardLink);
    }

    // CHANGED: Made public so StaffDashboardController can call them
    public void showMyResources() {
        loadView("/com/project/apdesktopapplication/staff-my-resources.fxml");
        updateActiveButton(myResourcesLink);
    }

    // CHANGED: Made public so StaffDashboardController can call them
    public void showPendingApprovals() {
        loadView("/com/project/apdesktopapplication/staff-pending-approvals.fxml");
        updateActiveButton(pendingApprovalsLink);
    }

    @FXML
    private void showAllResources() {
        loadView("/com/project/apdesktopapplication/staff-all-resources.fxml");
        updateActiveButton(allResourcesLink);
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

            // Pass user info and navigation controller to child controllers
            Object controller = loader.getController();
            if (controller instanceof StaffDashboardController) {
                ((StaffDashboardController) controller).setCurrentUser(currentUser);
                ((StaffDashboardController) controller).setNavigationController(this);
            } else if (controller instanceof StaffMyResourcesController) {
                ((StaffMyResourcesController) controller).setCurrentUser(currentUser);
            } else if (controller instanceof StaffPendingApprovalsController) {
                ((StaffPendingApprovalsController) controller).setCurrentUser(currentUser);
            } else if (controller instanceof StaffAllResourcesController) {
                ((StaffAllResourcesController) controller).setCurrentUser(currentUser);
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
        dashboardLink.setStyle(defaultStyle);
        myResourcesLink.setStyle(defaultStyle);
        pendingApprovalsLink.setStyle(defaultStyle);
        allResourcesLink.setStyle(defaultStyle);

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
package com.project.apdesktopapplication;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class AdminNavigationController {

    @FXML private StackPane contentArea;
    @FXML private Button dashboardLink;
    @FXML private Button pendingApprovalsLink;
    @FXML private Button manageResourcesLink;
    @FXML private Button manageUsersLink;

    private static AdminNavigationController instance;

    private static final String ACTIVE_STYLE =
            "-fx-background-color: #1F2937; -fx-text-fill: white; -fx-alignment: CENTER_LEFT; -fx-background-radius: 6; -fx-padding: 12 16 12 16; -fx-font-weight: bold;";
    private static final String INACTIVE_STYLE =
            "-fx-background-color: transparent; -fx-text-fill: #9CA3AF; -fx-alignment: CENTER_LEFT; -fx-background-radius: 6; -fx-padding: 12 16 12 16; -fx-font-weight: bold;";
    private static final String HOVER_STYLE =
            "-fx-background-color: #374151; -fx-text-fill: white; -fx-alignment: CENTER_LEFT; -fx-background-radius: 6; -fx-padding: 12 16 12 16; -fx-font-weight: bold;";

    @FXML
    public void initialize() {
        instance = this;
        showDashboard();
        setupHoverEffects();
    }

    private void setupHoverEffects() {
        Button[] buttons = {dashboardLink, pendingApprovalsLink, manageResourcesLink, manageUsersLink};
        for (Button btn : buttons) {
            btn.setOnMouseEntered(e -> {
                if (!btn.getStyle().equals(ACTIVE_STYLE)) {
                    btn.setStyle(HOVER_STYLE);
                }
            });
            btn.setOnMouseExited(e -> {
                if (!btn.getStyle().equals(ACTIVE_STYLE)) {
                    btn.setStyle(INACTIVE_STYLE);
                }
            });
        }
    }

    @FXML
    private void showDashboard() {
        loadPage("/com/project/apdesktopapplication/admin-dashboard.fxml");
        setActiveButton(dashboardLink);
    }

    @FXML
    private void showPendingApprovals() {
        loadPage("/com/project/apdesktopapplication/admin-pending-approvals.fxml");
        setActiveButton(pendingApprovalsLink);
    }

    @FXML
    private void showManageResources() {
        loadPage("/com/project/apdesktopapplication/admin-manage-resources.fxml");
        setActiveButton(manageResourcesLink);
    }

    @FXML
    private void showManageUsers() {
        loadPage("/com/project/apdesktopapplication/admin-manage-users.fxml");
        setActiveButton(manageUsersLink);
    }

    private void setActiveButton(Button active) {
        Button[] buttons = {dashboardLink, pendingApprovalsLink, manageResourcesLink, manageUsersLink};
        for (Button btn : buttons) {
            btn.setStyle(btn == active ? ACTIVE_STYLE : INACTIVE_STYLE);
        }
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

    public static void refreshDashboard() {
        if (instance != null) {
            instance.showDashboard();
        }
    }
}
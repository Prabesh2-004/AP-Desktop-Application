package com.project.apdesktopapplication;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class StaffController {

    @FXML private Button logoutBtn;
    @FXML private TextField searchField;
    @FXML private TableView<?> resourcesTable;

    @FXML private Button dashboardBtn;
    @FXML private Button resourcesBtn;
    @FXML private Button pendingBtn;
    @FXML private Button approvedBtn;
    @FXML private Button rejectedBtn;

    @FXML private VBox dashboardPage;
    @FXML private VBox resourcesPage;
    @FXML private VBox pendingPage;
    @FXML private VBox approvedPage;
    @FXML private VBox rejectedPage;

    @FXML
    public void initialize() {
        showPage(dashboardPage);
    }

    @FXML
    private void handleDashboard(ActionEvent event) {
        showPage(dashboardPage);
    }

    @FXML
    private void handleResources(ActionEvent event) {
        showPage(resourcesPage);
    }

    @FXML
    private void handlePending(ActionEvent event) {
        showPage(pendingPage);
    }

    @FXML
    private void handleApproved(ActionEvent event) {
        showPage(approvedPage);
    }

    @FXML
    private void handleRejected(ActionEvent event) {
        showPage(rejectedPage);
    }

    private void showPage(VBox page) {
        dashboardPage.setVisible(false);
        dashboardPage.setManaged(false);
        resourcesPage.setVisible(false);
        resourcesPage.setManaged(false);
        pendingPage.setVisible(false);
        pendingPage.setManaged(false);
        approvedPage.setVisible(false);
        approvedPage.setManaged(false);
        rejectedPage.setVisible(false);
        rejectedPage.setManaged(false);

        page.setVisible(true);
        page.setManaged(true);
    }
}

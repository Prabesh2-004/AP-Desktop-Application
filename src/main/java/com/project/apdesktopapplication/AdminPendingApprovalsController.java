package com.project.apdesktopapplication;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

public class AdminPendingApprovalsController {

    @FXML private TableView<BookingRequest> approvalsTable;
    @FXML private TableColumn<BookingRequest, String> colUser;
    @FXML private TableColumn<BookingRequest, String> colResource;
    @FXML private TableColumn<BookingRequest, String> colSchedule;
    @FXML private TableColumn<BookingRequest, String> colActions;
    @FXML private Label pendingCountLabel;

    private ObservableList<BookingRequest> pendingRequests;

    @FXML
    public void initialize() {
        setupColumns();
        loadPendingRequests();
    }

    private void setupColumns() {
        colUser.setCellValueFactory(new PropertyValueFactory<>("userInfo"));
        colResource.setCellValueFactory(new PropertyValueFactory<>("resourceInfo"));
        colSchedule.setCellValueFactory(new PropertyValueFactory<>("schedule"));

        colUser.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    String[] parts = item.split("\\|");
                    Label initials = new Label(parts[0]);
                    initials.setStyle("-fx-background-color: #E5E7EB; -fx-background-radius: 20; -fx-padding: 4 8 4 8; -fx-font-weight: bold; -fx-font-size: 12px;");
                    Label name = new Label(parts[1]);
                    name.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
                    Label role = new Label(parts[2]);
                    role.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 11px;");

                    HBox box = new HBox(8, initials, new javafx.scene.layout.VBox(2, name, role));
                    setGraphic(box);
                }
            }
        });

        colResource.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    String[] parts = item.split("\\|");
                    Label name = new Label(parts[0]);
                    name.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
                    Label location = new Label("📍 " + parts[1]);
                    location.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 11px;");

                    javafx.scene.layout.VBox box = new javafx.scene.layout.VBox(2, name, location);
                    setGraphic(box);
                }
            }
        });

        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button approveBtn = new Button("✓ Approve");
            private final Button rejectBtn = new Button("✕ Reject");
            {
                approveBtn.setStyle("-fx-background-color: #16A34A; -fx-text-fill: white; -fx-background-radius: 6; -fx-padding: 4 12 4 12; -fx-cursor: hand;");
                approveBtn.setOnMouseEntered(e -> approveBtn.setStyle("-fx-background-color: #15803D; -fx-text-fill: white; -fx-background-radius: 6; -fx-padding: 4 12 4 12; -fx-cursor: hand;"));
                approveBtn.setOnMouseExited(e -> approveBtn.setStyle("-fx-background-color: #16A34A; -fx-text-fill: white; -fx-background-radius: 6; -fx-padding: 4 12 4 12; -fx-cursor: hand;"));

                rejectBtn.setStyle("-fx-background-color: #DC2626; -fx-text-fill: white; -fx-background-radius: 6; -fx-padding: 4 12 4 12; -fx-cursor: hand;");
                rejectBtn.setOnMouseEntered(e -> rejectBtn.setStyle("-fx-background-color: #B91C1C; -fx-text-fill: white; -fx-background-radius: 6; -fx-padding: 4 12 4 12; -fx-cursor: hand;"));
                rejectBtn.setOnMouseExited(e -> rejectBtn.setStyle("-fx-background-color: #DC2626; -fx-text-fill: white; -fx-background-radius: 6; -fx-padding: 4 12 4 12; -fx-cursor: hand;"));

                approveBtn.setOnAction(e -> {
                    BookingRequest request = getTableView().getItems().get(getIndex());
                    approveRequest(request);
                });

                rejectBtn.setOnAction(e -> {
                    BookingRequest request = getTableView().getItems().get(getIndex());
                    rejectRequest(request);
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox box = new HBox(8, approveBtn, rejectBtn);
                    setGraphic(box);
                }
            }
        });
    }

    private void loadPendingRequests() {
        pendingRequests = FXCollections.observableArrayList(
                new BookingRequest("JS|James Smith|Graduate Researcher", "Optics Lab 402|Science Wing B", "Oct 24, 2023 14:00 - 18:00"),
                new BookingRequest("MW|Maya Wong|Staff Faculty", "Conference Room C|Admin Hall", "Oct 25, 2023 09:00 - 11:00"),
                new BookingRequest("AR|Alex Rivera|Post-Doc Fellow", "3D Print Station 2|Makerspace", "Oct 26, 2023 13:30 - 15:30"),
                new BookingRequest("TK|Thomas Kim|PhD Student", "Bio Lab 203|Life Sciences", "Oct 27, 2023 10:00 - 12:00"),
                new BookingRequest("LD|Lisa Davis|Research Assistant", "Data Center Pod 5|IT Building", "Oct 28, 2023 15:00 - 17:00")
        );
        approvalsTable.setItems(pendingRequests);
        pendingCountLabel.setText(String.valueOf(pendingRequests.size()));
    }

    private void approveRequest(BookingRequest request) {
        pendingRequests.remove(request);
        pendingCountLabel.setText(String.valueOf(pendingRequests.size()));
        // In real app, save to database
        System.out.println("Approved: " + request.getResourceInfo());
    }

    private void rejectRequest(BookingRequest request) {
        pendingRequests.remove(request);
        pendingCountLabel.setText(String.valueOf(pendingRequests.size()));
        // In real app, save to database
        System.out.println("Rejected: " + request.getResourceInfo());
    }

    // Model class for booking requests
    public static class BookingRequest {
        private final String userInfo;
        private final String resourceInfo;
        private final String schedule;

        public BookingRequest(String userInfo, String resourceInfo, String schedule) {
            this.userInfo = userInfo;
            this.resourceInfo = resourceInfo;
            this.schedule = schedule;
        }

        public String getUserInfo() { return userInfo; }
        public String getResourceInfo() { return resourceInfo; }
        public String getSchedule() { return schedule; }
    }
}
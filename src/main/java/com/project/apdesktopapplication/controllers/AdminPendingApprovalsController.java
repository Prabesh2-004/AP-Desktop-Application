package com.project.apdesktopapplication.controllers;

import com.project.apdesktopapplication.models.Booking;
import com.project.apdesktopapplication.models.Resource;
import com.project.apdesktopapplication.models.User;
import com.project.apdesktopapplication.services.BookingService;
import com.project.apdesktopapplication.services.ResourceService;
import com.project.apdesktopapplication.services.UserService;
import com.project.apdesktopapplication.exceptions.UnauthorizedAccessException;
import com.project.apdesktopapplication.utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

public class AdminPendingApprovalsController {
    @FXML private Label pendingCountLabel;
    @FXML private TableView<Booking> approvalsTable;
    @FXML private TableColumn<Booking, String> colUser;
    @FXML private TableColumn<Booking, String> colResource;
    @FXML private TableColumn<Booking, String> colSchedule;
    @FXML private TableColumn<Booking, Void> colActions;

    private BookingService bookingService = BookingService.getInstance();
    private UserService userService = UserService.getInstance();
    private ResourceService resourceService = ResourceService.getInstance();
    private ObservableList<Booking> pendingBookings;

    @FXML
    public void initialize() {
        setupTable();
        loadData();
    }

    private void setupTable() {
        colUser.setCellValueFactory(new PropertyValueFactory<>("userId"));
        colResource.setCellValueFactory(new PropertyValueFactory<>("resourceId"));
        colSchedule.setCellValueFactory(new PropertyValueFactory<>("date"));

        colUser.setCellFactory(column -> new TableCell<Booking, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    User user = userService.getUserById(item);
                    setText(user != null ? user.getFullName() + " (" + user.getUsername() + ")" : item);
                }
            }
        });

        colResource.setCellFactory(column -> new TableCell<Booking, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    Resource resource = resourceService.getResourceById(item);
                    setText(resource != null ? resource.getName() + " (" + resource.getType() + ")" : item);
                }
            }
        });

        colSchedule.setCellFactory(column -> new TableCell<Booking, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    Booking booking = getTableView().getItems().get(getIndex());
                    if (booking != null) {
                        setText(booking.getDate() + " " + booking.getStartTime() + "-" + booking.getEndTime());
                    } else {
                        setText(item);
                    }
                }
            }
        });

        colActions.setCellFactory(column -> new TableCell<Booking, Void>() {
            private final Button approveBtn = new Button("✓ Approve");
            private final Button rejectBtn = new Button("✗ Reject");

            {
                approveBtn.setStyle("-fx-background-color: #16A34A; -fx-text-fill: white; -fx-background-radius: 4; -fx-padding: 4 10 4 10; -fx-cursor: hand;");
                rejectBtn.setStyle("-fx-background-color: #DC2626; -fx-text-fill: white; -fx-background-radius: 4; -fx-padding: 4 10 4 10; -fx-cursor: hand;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }

                Booking booking = getTableView().getItems().get(getIndex());
                if (booking != null && booking.getStatus().equals("PENDING")) {
                    approveBtn.setOnAction(e -> handleApprove(booking));
                    rejectBtn.setOnAction(e -> handleReject(booking));
                    HBox box = new HBox(6, approveBtn, rejectBtn);
                    setGraphic(box);
                } else {
                    setGraphic(null);
                }
            }
        });
    }

    private void loadData() {
        pendingBookings = FXCollections.observableArrayList(bookingService.getPendingBookings());
        approvalsTable.setItems(pendingBookings);
        pendingCountLabel.setText(String.valueOf(pendingBookings.size()));
    }

    private void handleApprove(Booking booking) {
        try {
            User actor = SessionManager.getInstance().getCurrentUser();
            // Service verifies the actor may approve (polymorphic canApproveBookings())
            // and updates the linked resource status internally.
            bookingService.approveBooking(actor, booking.getBookingId());
            loadData();
        } catch (UnauthorizedAccessException e) {
            showError(e.getMessage());
        }
    }

    private void handleReject(Booking booking) {
        try {
            User actor = SessionManager.getInstance().getCurrentUser();
            bookingService.rejectBooking(actor, booking.getBookingId());
            loadData();
        } catch (UnauthorizedAccessException e) {
            showError(e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Action not allowed");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void refresh() {
        loadData();
    }
}
package com.project.apdesktopapplication.controllers;

import com.project.apdesktopapplication.models.Booking;
import com.project.apdesktopapplication.models.Resource;
import com.project.apdesktopapplication.models.User;
import com.project.apdesktopapplication.services.BookingService;
import com.project.apdesktopapplication.services.ResourceService;
import com.project.apdesktopapplication.utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class BookingController {
    @FXML private TableView<Booking> bookingsTable;
    @FXML private TableColumn<Booking, String> colBookingId;
    @FXML private TableColumn<Booking, String> colResource;
    @FXML private TableColumn<Booking, String> colDate;
    @FXML private TableColumn<Booking, String> colTime;
    @FXML private TableColumn<Booking, String> colStatus;
    @FXML private TableColumn<Booking, Void> colActions;
    @FXML private Label totalBookingsLabel;

    private BookingService bookingService = BookingService.getInstance();
    private ResourceService resourceService = ResourceService.getInstance();
    private ObservableList<Booking> userBookings;
    private User currentUser;

    @FXML
    public void initialize() {
        currentUser = SessionManager.getInstance().getCurrentUser();
        setupTable();
        loadBookings();
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (user != null) {
            loadBookings();
        }
    }

    private void setupTable() {
        colBookingId.setCellValueFactory(new PropertyValueFactory<>("bookingId"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colTime.setCellValueFactory(cellData -> {
            Booking booking = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(
                    booking.getStartTime() + " - " + booking.getEndTime()
            );
        });

        // Resource name column
        colResource.setCellFactory(column -> new TableCell<Booking, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    Booking booking = getTableView().getItems().get(getIndex());
                    if (booking != null) {
                        Resource resource = resourceService.getResourceById(booking.getResourceId());
                        setText(resource != null ? resource.getName() : booking.getResourceId());
                    } else {
                        setText(item);
                    }
                }
            }
        });
        colResource.setCellValueFactory(new PropertyValueFactory<>("resourceId"));

        // Status column with color coding
        colStatus.setCellFactory(column -> new TableCell<Booking, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    switch (item) {
                        case "APPROVED":
                            setStyle("-fx-text-fill: #16A34A; -fx-font-weight: bold; -fx-background-color: #D1FAE5; -fx-padding: 2 8 2 8; -fx-background-radius: 4;");
                            break;
                        case "PENDING":
                            setStyle("-fx-text-fill: #EAB308; -fx-font-weight: bold; -fx-background-color: #FEF3C7; -fx-padding: 2 8 2 8; -fx-background-radius: 4;");
                            break;
                        case "REJECTED":
                            setStyle("-fx-text-fill: #DC2626; -fx-font-weight: bold; -fx-background-color: #FEE2E2; -fx-padding: 2 8 2 8; -fx-background-radius: 4;");
                            break;
                        case "CANCELLED":
                            setStyle("-fx-text-fill: #6B7280; -fx-font-weight: bold; -fx-background-color: #F3F4F6; -fx-padding: 2 8 2 8; -fx-background-radius: 4;");
                            break;
                        default:
                            setStyle("");
                    }
                }
            }
        });
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Actions column - Cancel button for pending bookings
        colActions.setCellFactory(column -> new TableCell<Booking, Void>() {
            private final Button cancelBtn = new Button("✕ Cancel");

            {
                cancelBtn.setStyle("-fx-background-color: #DC2626; -fx-text-fill: white; -fx-background-radius: 4; -fx-padding: 4 10 4 10; -fx-cursor: hand; -fx-font-weight: bold;");
                cancelBtn.setOnAction(e -> {
                    Booking booking = getTableView().getItems().get(getIndex());
                    if (booking != null && "PENDING".equals(booking.getStatus())) {
                        handleCancelBooking(booking);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }

                Booking booking = getTableView().getItems().get(getIndex());
                if (booking != null && "PENDING".equals(booking.getStatus())) {
                    setGraphic(cancelBtn);
                } else {
                    setGraphic(null);
                }
            }
        });
    }

    private void loadBookings() {
        if (currentUser == null) {
            userBookings = FXCollections.observableArrayList();
            bookingsTable.setItems(userBookings);
            totalBookingsLabel.setText("0");
            return;
        }

        List<Booking> bookings = bookingService.getBookingsByUserId(currentUser.getUserId());
        userBookings = FXCollections.observableArrayList(bookings);
        bookingsTable.setItems(userBookings);
        totalBookingsLabel.setText(String.valueOf(bookings.size()));
    }

    private void handleCancelBooking(Booking booking) {
        booking.setStatus("CANCELLED");
        bookingService.updateBooking(booking);

        // If the booking was approved, free up the resource
        if ("APPROVED".equals(booking.getStatus())) {
            Resource resource = resourceService.getResourceById(booking.getResourceId());
            if (resource != null) {
                resource.setStatus("AVAILABLE");
                resourceService.updateResource(resource);
            }
        }

        loadBookings();
    }

    @FXML
    private void refreshBookings() {
        loadBookings();
    }
}
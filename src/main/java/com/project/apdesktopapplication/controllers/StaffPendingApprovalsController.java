package com.project.apdesktopapplication.controllers;

import com.project.apdesktopapplication.models.Booking;
import com.project.apdesktopapplication.models.Resource;
import com.project.apdesktopapplication.models.User;
import com.project.apdesktopapplication.services.BookingService;
import com.project.apdesktopapplication.services.ResourceService;
import com.project.apdesktopapplication.services.UserService;
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

import java.util.List;
import java.util.stream.Collectors;

public class StaffPendingApprovalsController {
    @FXML private Label pendingCountLabel;
    @FXML private TableView<Booking> approvalsTable;
    @FXML private TableColumn<Booking, String> colUser;
    @FXML private TableColumn<Booking, String> colResource;
    @FXML private TableColumn<Booking, String> colSchedule;
    @FXML private TableColumn<Booking, String> colStatus;
    @FXML private TableColumn<Booking, Void> colActions;

    private User currentUser;
    private BookingService bookingService = BookingService.getInstance();
    private ResourceService resourceService = ResourceService.getInstance();
    private UserService userService = UserService.getInstance();
    private ObservableList<Booking> pendingBookings;

    public void setCurrentUser(User user) {
        this.currentUser = user;
        loadData();
    }

    @FXML
    public void initialize() {
        setupTable();
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
                        case "PENDING":
                            setStyle("-fx-text-fill: #EAB308; -fx-font-weight: bold; -fx-background-color: #FEF3C7; -fx-padding: 2 8 2 8; -fx-background-radius: 4;");
                            break;
                        case "APPROVED":
                            setStyle("-fx-text-fill: #16A34A; -fx-font-weight: bold; -fx-background-color: #D1FAE5; -fx-padding: 2 8 2 8; -fx-background-radius: 4;");
                            break;
                        case "REJECTED":
                            setStyle("-fx-text-fill: #DC2626; -fx-font-weight: bold; -fx-background-color: #FEE2E2; -fx-padding: 2 8 2 8; -fx-background-radius: 4;");
                            break;
                        default:
                            setStyle("");
                    }
                }
            }
        });
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

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
                if (booking != null && "PENDING".equals(booking.getStatus())) {
                    // Check if this booking is for a resource owned by the staff
                    Resource resource = resourceService.getResourceById(booking.getResourceId());
                    if (resource != null && resource.getCreatorId().equals(currentUser.getUserId())) {
                        approveBtn.setOnAction(e -> handleApprove(booking));
                        rejectBtn.setOnAction(e -> handleReject(booking));
                        HBox box = new HBox(6, approveBtn, rejectBtn);
                        setGraphic(box);
                    } else {
                        setGraphic(null);
                    }
                } else {
                    setGraphic(null);
                }
            }
        });
    }

    private void loadData() {
        if (currentUser == null) return;

        // Get staff's resources
        List<String> staffResourceIds = resourceService.getAllResources().stream()
                .filter(r -> r.getCreatorId().equals(currentUser.getUserId()))
                .map(Resource::getResourceId)
                .collect(Collectors.toList());

        // Get pending bookings for staff's resources
        List<Booking> bookings = bookingService.getAllBookings().stream()
                .filter(b -> staffResourceIds.contains(b.getResourceId()))
                .filter(b -> "PENDING".equals(b.getStatus()))
                .collect(Collectors.toList());

        pendingBookings = FXCollections.observableArrayList(bookings);
        approvalsTable.setItems(pendingBookings);
        pendingCountLabel.setText(String.valueOf(bookings.size()));
    }

    private void handleApprove(Booking booking) {
        booking.setStatus("APPROVED");
        bookingService.updateBooking(booking);

        // Update resource status
        Resource resource = resourceService.getResourceById(booking.getResourceId());
        if (resource != null) {
            resource.setStatus("BOOKED");
            resourceService.updateResource(resource);
        }

        loadData();
    }

    private void handleReject(Booking booking) {
        booking.setStatus("REJECTED");
        bookingService.updateBooking(booking);
        loadData();
    }

    @FXML
    private void refresh() {
        loadData();
    }
}
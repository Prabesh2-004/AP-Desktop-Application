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
import javafx.scene.layout.VBox;

public class ResourceController {

    @FXML private TableView<Resource> resourceTable;
    @FXML private TableColumn<Resource, String> colId;
    @FXML private TableColumn<Resource, String> colName;
    @FXML private TableColumn<Resource, String> colType;
    @FXML private TableColumn<Resource, String> colCapacity;
    @FXML private TableColumn<Resource, String> colStatus;
    @FXML private TableColumn<Resource, String> colActions;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("resourceId"));
        colCapacity.setCellValueFactory(new PropertyValueFactory<>("capacity"));

        // Name + location stacked in one cell
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colName.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String name, boolean empty) {
                super.updateItem(name, empty);
                if (empty || name == null) {
                    setGraphic(null);
                } else {
                    Resource r = getTableView().getItems().get(getIndex());
                    Label nameLabel = new Label(r.getName());
                    nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                    Label locLabel = new Label("📍 " + r.getLocation());
                    locLabel.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 11px;");
                    VBox box = new VBox(2, nameLabel, locLabel);
                    setGraphic(box);
                }
            }
        });

        // Type as a pill/badge
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colType.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String type, boolean empty) {
                super.updateItem(type, empty);
                if (empty || type == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(type);
                    setStyle("-fx-text-fill: #374151; -fx-font-size: 11px; -fx-font-weight: bold; -fx-background-color: #F3F4F6; -fx-background-radius: 12px; -fx-padding: 2 10 2 10;");
                }
            }
        });

        // Status colored: green Available, red Occupied/Booked, yellow Maintenance
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");

                    // Status indicators
                    String statusLower = status.toLowerCase();
                    if (statusLower.equals("available")) {
                        setStyle(getStyle() + " -fx-text-fill: #16A34A;");
                        setText("● " + status);
                    } else if (statusLower.equals("occupied") || statusLower.equals("booked")) {
                        setStyle(getStyle() + " -fx-text-fill: #DC2626;");
                        setText("● " + status);
                    } else if (statusLower.equals("maintenance")) {
                        setStyle(getStyle() + " -fx-text-fill: #EAB308;");
                        setText("● " + status);
                    } else {
                        setStyle(getStyle() + " -fx-text-fill: #9CA3AF;");
                    }
                }
            }
        });

        // Actions: black Book Now button
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button bookBtn = new Button("Book Now");
            {
                bookBtn.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-background-radius: 6; -fx-padding: 6 16 6 16; -fx-cursor: hand;");
                bookBtn.setOnMouseEntered(e -> {
                    if (!bookBtn.isDisabled()) {
                        bookBtn.setStyle("-fx-background-color: #1F2937; -fx-text-fill: white; -fx-background-radius: 6; -fx-padding: 6 16 6 16; -fx-cursor: hand;");
                    }
                });
                bookBtn.setOnMouseExited(e -> {
                    if (!bookBtn.isDisabled()) {
                        bookBtn.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-background-radius: 6; -fx-padding: 6 16 6 16; -fx-cursor: hand;");
                    }
                });
                bookBtn.setOnAction(e -> {
                    Resource r = getTableView().getItems().get(getIndex());
                    NavigationController.goToBooking(r);
                });
            }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Resource r = getTableView().getItems().get(getIndex());
                    boolean isAvailable = r.getStatus().equalsIgnoreCase("Available");
                    bookBtn.setDisable(!isAvailable);
                    if (!isAvailable) {
                        bookBtn.setStyle("-fx-background-color: #D1D5DB; -fx-text-fill: #9CA3AF; -fx-background-radius: 6; -fx-padding: 6 16 6 16;");
                    }
                    setGraphic(bookBtn);
                }
            }
        });

        resourceTable.setItems(loadDummyData());
    }

    private ObservableList<Resource> loadDummyData() {
        return FXCollections.observableArrayList(
                new Resource("LAB-N304", "Advanced Physics Lab", "North Wing, 3rd Floor", "Laboratory", "24", "Available", "Dr. Sarah Chen"),
                new Resource("HALL-C01", "Great Hall Auditorium", "Central Administration", "Auditorium", "500", "Booked", "Prof. James Wilson"),
                new Resource("CONF-S12", "Dean's Meeting Room", "South Wing, 1st Floor", "Conference", "12", "Maintenance", "Dr. Emily Brown"),
                new Resource("STUD-E40", "Digital Media Suite", "East Wing, 4th Floor", "Multimedia", "8", "Available", "Prof. Michael Davis"),
                new Resource("LIB-042B", "Library Study Pod B", "Library Commons, Basement", "Study Pod", "4", "Available", "Librarian Anna Martinez")
        );
    }
}
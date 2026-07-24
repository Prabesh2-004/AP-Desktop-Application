package com.project.apdesktopapplication;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class AdminManageResourcesController {

    @FXML private TableView<Resource> resourcesTable;
    @FXML private TableColumn<Resource, String> colId;
    @FXML private TableColumn<Resource, String> colName;
    @FXML private TableColumn<Resource, String> colType;
    @FXML private TableColumn<Resource, String> colCapacity;
    @FXML private TableColumn<Resource, String> colStatus;
    @FXML private TableColumn<Resource, String> colActions;
    @FXML private Label totalResourcesLabel;

    private ObservableList<Resource> resources;

    @FXML
    public void initialize() {
        setupColumns();
        loadResources();
    }

    private void setupColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("resourceId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colCapacity.setCellValueFactory(new PropertyValueFactory<>("capacity"));

        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    String statusLower = status.toLowerCase();
                    if (statusLower.equals("available")) {
                        setStyle("-fx-text-fill: #16A34A; -fx-font-weight: bold; -fx-font-size: 12px;");
                        setText("● " + status);
                    } else if (statusLower.equals("booked")) {
                        setStyle("-fx-text-fill: #DC2626; -fx-font-weight: bold; -fx-font-size: 12px;");
                        setText("● " + status);
                    } else if (statusLower.equals("maintenance")) {
                        setStyle("-fx-text-fill: #EAB308; -fx-font-weight: bold; -fx-font-size: 12px;");
                        setText("● " + status);
                    }
                }
            }
        });

        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("✎ Edit");
            private final Button deleteBtn = new Button("✕ Delete");
            {
                editBtn.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-background-radius: 6; -fx-padding: 4 12 4 12; -fx-cursor: hand;");
                editBtn.setOnMouseEntered(e -> editBtn.setStyle("-fx-background-color: #1F2937; -fx-text-fill: white; -fx-background-radius: 6; -fx-padding: 4 12 4 12; -fx-cursor: hand;"));
                editBtn.setOnMouseExited(e -> editBtn.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-background-radius: 6; -fx-padding: 4 12 4 12; -fx-cursor: hand;"));

                deleteBtn.setStyle("-fx-background-color: #DC2626; -fx-text-fill: white; -fx-background-radius: 6; -fx-padding: 4 12 4 12; -fx-cursor: hand;");
                deleteBtn.setOnMouseEntered(e -> deleteBtn.setStyle("-fx-background-color: #B91C1C; -fx-text-fill: white; -fx-background-radius: 6; -fx-padding: 4 12 4 12; -fx-cursor: hand;"));
                deleteBtn.setOnMouseExited(e -> deleteBtn.setStyle("-fx-background-color: #DC2626; -fx-text-fill: white; -fx-background-radius: 6; -fx-padding: 4 12 4 12; -fx-cursor: hand;"));

                editBtn.setOnAction(e -> {
                    Resource resource = getTableView().getItems().get(getIndex());
                    showEditResourceDialog(resource);
                });

                deleteBtn.setOnAction(e -> {
                    Resource resource = getTableView().getItems().get(getIndex());
                    deleteResource(resource);
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox box = new HBox(8, editBtn, deleteBtn);
                    setGraphic(box);
                }
            }
        });
    }

    private void loadResources() {
        resources = FXCollections.observableArrayList(
                new Resource("LAB-N304", "Advanced Physics Lab", "Laboratory", "24", "Available", "Dr. Sarah Chen"),
                new Resource("HALL-C01", "Great Hall Auditorium", "Auditorium", "500", "Booked", "Prof. James Wilson"),
                new Resource("CONF-S12", "Dean's Meeting Room", "Conference", "12", "Maintenance", "Dr. Emily Brown"),
                new Resource("STUD-E40", "Digital Media Suite", "Multimedia", "8", "Available", "Prof. Michael Davis"),
                new Resource("LIB-042B", "Library Study Pod B", "Study Pod", "4", "Available", "Librarian Anna Martinez"),
                new Resource("BIO-203", "Bio Lab 203", "Laboratory", "20", "Booked", "Dr. Robert Chen")
        );
        resourcesTable.setItems(resources);
        totalResourcesLabel.setText(String.valueOf(resources.size()));
    }

    @FXML
    private void handleAddResource() {
        showAddResourceDialog();
    }

    private void showAddResourceDialog() {
        Dialog<Resource> dialog = new Dialog<>();
        dialog.setTitle("Add New Resource");
        dialog.setHeaderText("Enter resource details");

        VBox content = new VBox(10);
        content.setStyle("-fx-padding: 20;");

        TextField idField = new TextField();
        idField.setPromptText("Resource ID");
        TextField nameField = new TextField();
        nameField.setPromptText("Name");
        TextField typeField = new TextField();
        typeField.setPromptText("Type");
        TextField capacityField = new TextField();
        capacityField.setPromptText("Capacity");
        ComboBox<String> statusBox = new ComboBox<>();
        statusBox.getItems().addAll("Available", "Booked", "Maintenance");
        statusBox.setPromptText("Status");
        TextField creatorField = new TextField();
        creatorField.setPromptText("Creator Name");

        content.getChildren().addAll(
                new Label("Resource ID:"), idField,
                new Label("Name:"), nameField,
                new Label("Type:"), typeField,
                new Label("Capacity:"), capacityField,
                new Label("Status:"), statusBox,
                new Label("Creator Name:"), creatorField
        );

        dialog.getDialogPane().setContent(content);

        Button addButton = new Button("Add");
        addButton.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-padding: 8 20 8 20; -fx-cursor: hand;");
        addButton.setOnAction(e -> {
            if (!idField.getText().isEmpty() && !nameField.getText().isEmpty()) {
                Resource newResource = new Resource(
                        idField.getText(),
                        nameField.getText(),
                        typeField.getText(),
                        capacityField.getText(),
                        statusBox.getValue() != null ? statusBox.getValue() : "Available",
                        creatorField.getText()
                );
                resources.add(newResource);
                totalResourcesLabel.setText(String.valueOf(resources.size()));
                dialog.close();
            }
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-background-color: #E5E7EB; -fx-text-fill: #374151; -fx-padding: 8 20 8 20; -fx-cursor: hand;");
        cancelButton.setOnAction(e -> dialog.close());

        HBox buttonBox = new HBox(10, addButton, cancelButton);
        content.getChildren().add(buttonBox);

        dialog.showAndWait();
    }

    private void showEditResourceDialog(Resource resource) {
        Dialog<Resource> dialog = new Dialog<>();
        dialog.setTitle("Edit Resource");
        dialog.setHeaderText("Update resource details for: " + resource.getName());

        VBox content = new VBox(10);
        content.setStyle("-fx-padding: 20;");

        TextField nameField = new TextField(resource.getName());
        TextField typeField = new TextField(resource.getType());
        TextField capacityField = new TextField(resource.getCapacity());
        ComboBox<String> statusBox = new ComboBox<>();
        statusBox.getItems().addAll("Available", "Booked", "Maintenance");
        statusBox.setValue(resource.getStatus());

        content.getChildren().addAll(
                new Label("Name:"), nameField,
                new Label("Type:"), typeField,
                new Label("Capacity:"), capacityField,
                new Label("Status:"), statusBox
        );

        dialog.getDialogPane().setContent(content);

        Button saveButton = new Button("Save");
        saveButton.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-padding: 8 20 8 20; -fx-cursor: hand;");
        saveButton.setOnAction(e -> {
            // Update resource fields
            // In real app, this would update the database
            resource.setName(nameField.getText());
            resource.setType(typeField.getText());
            resource.setCapacity(capacityField.getText());
            resource.setStatus(statusBox.getValue());
            resourcesTable.refresh();
            dialog.close();
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-background-color: #E5E7EB; -fx-text-fill: #374151; -fx-padding: 8 20 8 20; -fx-cursor: hand;");
        cancelButton.setOnAction(e -> dialog.close());

        HBox buttonBox = new HBox(10, saveButton, cancelButton);
        content.getChildren().add(buttonBox);

        dialog.showAndWait();
    }

    private void deleteResource(Resource resource) {
        resources.remove(resource);
        totalResourcesLabel.setText(String.valueOf(resources.size()));
    }
}
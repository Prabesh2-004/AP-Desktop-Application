package com.project.apdesktopapplication.controllers;

import com.project.apdesktopapplication.models.Resource;
import com.project.apdesktopapplication.services.ResourceService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonType;
import javafx.geometry.Insets;

import java.util.Optional;

public class AdminManageResourcesController {
    @FXML private Label totalResourcesLabel;
    @FXML private TableView<Resource> resourcesTable;
    @FXML private TableColumn<Resource, String> colId;
    @FXML private TableColumn<Resource, String> colName;
    @FXML private TableColumn<Resource, String> colType;
    @FXML private TableColumn<Resource, Integer> colCapacity;
    @FXML private TableColumn<Resource, String> colStatus;
    @FXML private TableColumn<Resource, Void> colActions;

    private ResourceService resourceService = ResourceService.getInstance();
    private ObservableList<Resource> resources;

    @FXML
    public void initialize() {
        setupTable();
        loadData();
    }

    private void setupTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("resourceId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colCapacity.setCellValueFactory(new PropertyValueFactory<>("capacity"));

        colStatus.setCellFactory(column -> new TableCell<Resource, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    switch (item) {
                        case "AVAILABLE":
                            setStyle("-fx-text-fill: #16A34A; -fx-font-weight: bold;");
                            break;
                        case "MAINTENANCE":
                            setStyle("-fx-text-fill: #EAB308; -fx-font-weight: bold;");
                            break;
                        case "BOOKED":
                            setStyle("-fx-text-fill: #DC2626; -fx-font-weight: bold;");
                            break;
                        default:
                            setStyle("");
                    }
                }
            }
        });

        colActions.setCellFactory(column -> new TableCell<Resource, Void>() {
            private final Button editBtn = new Button("✎ Edit");
            private final Button deleteBtn = new Button("✕ Delete");

            {
                editBtn.setStyle("-fx-background-color: #3B82F6; -fx-text-fill: white; -fx-background-radius: 4; -fx-padding: 4 10 4 10; -fx-cursor: hand;");
                deleteBtn.setStyle("-fx-background-color: #DC2626; -fx-text-fill: white; -fx-background-radius: 4; -fx-padding: 4 10 4 10; -fx-cursor: hand;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }

                Resource resource = getTableView().getItems().get(getIndex());
                editBtn.setOnAction(e -> handleEditResource(resource));
                deleteBtn.setOnAction(e -> handleDeleteResource(resource));
                HBox box = new HBox(6, editBtn, deleteBtn);
                setGraphic(box);
            }
        });
    }

    private void loadData() {
        resources = FXCollections.observableArrayList(resourceService.getAllResources());
        resourcesTable.setItems(resources);
        totalResourcesLabel.setText(String.valueOf(resources.size()));
    }

    @FXML
    private void handleAddResource() {
        showResourceDialog(null);
    }

    private void handleEditResource(Resource resource) {
        showResourceDialog(resource);
    }

    private void handleDeleteResource(Resource resource) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Delete Resource");
        dialog.setContentText("Are you sure you want to delete " + resource.getName() + "?");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            resourceService.deleteResource(resource.getResourceId());
            loadData();
        }
    }

    private void showResourceDialog(Resource existingResource) {
        Dialog<Resource> dialog = new Dialog<>();
        dialog.setTitle(existingResource == null ? "Add Resource" : "Edit Resource");
        dialog.setHeaderText(existingResource == null ? "Create a new resource" : "Update resource information");

        VBox form = new VBox(10);
        form.setPadding(new Insets(20));

        TextField nameField = new TextField();
        nameField.setPromptText("Resource Name");
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("Meeting Room", "Lab", "Event Space", "Study Room", "Equipment", "Other");
        typeCombo.setPromptText("Type");
        TextField locationField = new TextField();
        locationField.setPromptText("Location");
        TextField capacityField = new TextField();
        capacityField.setPromptText("Capacity");
        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("AVAILABLE", "MAINTENANCE", "BOOKED");
        statusCombo.setPromptText("Status");

        if (existingResource != null) {
            nameField.setText(existingResource.getName());
            typeCombo.setValue(existingResource.getType());
            locationField.setText(existingResource.getLocation());
            capacityField.setText(String.valueOf(existingResource.getCapacity()));
            statusCombo.setValue(existingResource.getStatus());
        }

        form.getChildren().addAll(
                new Label("Name:"), nameField,
                new Label("Type:"), typeCombo,
                new Label("Location:"), locationField,
                new Label("Capacity:"), capacityField,
                new Label("Status:"), statusCombo
        );

        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                try {
                    String name = nameField.getText().trim();
                    String type = typeCombo.getValue();
                    String location = locationField.getText().trim();
                    int capacity = Integer.parseInt(capacityField.getText().trim());
                    String status = statusCombo.getValue();

                    if (name.isEmpty() || type == null || location.isEmpty() || status == null) {
                        return null;
                    }

                    Resource resource;
                    if (existingResource != null) {
                        resource = existingResource;
                        resource.setName(name);
                        resource.setType(type);
                        resource.setLocation(location);
                        resource.setCapacity(capacity);
                        resource.setStatus(status);
                    } else {
                        resource = Resource.create(null, name, type, location, capacity, status, "ADMIN001");
                    }
                    return resource;
                } catch (Exception e) {
                    return null;
                }
            }
            return null;
        });

        Optional<Resource> result = dialog.showAndWait();
        result.ifPresent(resource -> {
            if (existingResource != null) {
                resourceService.updateResource(resource);
            } else {
                resourceService.addResource(resource);
            }
            loadData();
        });
    }
}
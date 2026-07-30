package com.project.apdesktopapplication.controllers;

import com.project.apdesktopapplication.models.Resource;
import com.project.apdesktopapplication.models.User;
import com.project.apdesktopapplication.services.ResourceService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class StaffMyResourcesController {
    @FXML private Label myResourcesLabel;
    @FXML private TableView<Resource> resourcesTable;
    @FXML private TableColumn<Resource, String> colId;
    @FXML private TableColumn<Resource, String> colName;
    @FXML private TableColumn<Resource, String> colType;
    @FXML private TableColumn<Resource, String> colLocation;
    @FXML private TableColumn<Resource, Integer> colCapacity;
    @FXML private TableColumn<Resource, String> colStatus;
    @FXML private TableColumn<Resource, Void> colActions;

    private User currentUser;
    private ResourceService resourceService = ResourceService.getInstance();
    private ObservableList<Resource> myResources;

    public void setCurrentUser(User user) {
        this.currentUser = user;
        loadData();
    }

    @FXML
    public void initialize() {
        setupTable();
    }

    private void setupTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("resourceId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
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
                            setStyle("-fx-text-fill: #16A34A; -fx-font-weight: bold; -fx-background-color: #D1FAE5; -fx-padding: 2 8 2 8; -fx-background-radius: 4;");
                            break;
                        case "MAINTENANCE":
                            setStyle("-fx-text-fill: #EAB308; -fx-font-weight: bold; -fx-background-color: #FEF3C7; -fx-padding: 2 8 2 8; -fx-background-radius: 4;");
                            break;
                        case "BOOKED":
                            setStyle("-fx-text-fill: #DC2626; -fx-font-weight: bold; -fx-background-color: #FEE2E2; -fx-padding: 2 8 2 8; -fx-background-radius: 4;");
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
                // Only show actions if current user is the creator
                if (resource != null && resource.getCreatorId().equals(currentUser.getUserId())) {
                    editBtn.setOnAction(e -> handleEditResource(resource));
                    deleteBtn.setOnAction(e -> handleDeleteResource(resource));
                    HBox box = new HBox(6, editBtn, deleteBtn);
                    setGraphic(box);
                } else {
                    setGraphic(null);
                }
            }
        });
    }

    private void loadData() {
        if (currentUser == null) return;

        List<Resource> resources = resourceService.getAllResources().stream()
                .filter(r -> r.getCreatorId().equals(currentUser.getUserId()))
                .collect(Collectors.toList());

        myResources = FXCollections.observableArrayList(resources);
        resourcesTable.setItems(myResources);
        myResourcesLabel.setText(String.valueOf(resources.size()));
    }

    @FXML
    private void handleAddResource() {
        showResourceDialog(null);
    }

    private void handleEditResource(Resource resource) {
        showResourceDialog(resource);
    }

    private void handleDeleteResource(Resource resource) {
        // Only allow deletion if user is the creator
        if (!resource.getCreatorId().equals(currentUser.getUserId())) {
            showAlert("Permission Denied", "You can only delete resources you created.");
            return;
        }

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
                        // New resource - set creator as current user
                        resource = Resource.create(null, name, type, location, capacity, status, currentUser.getUserId());
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

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}